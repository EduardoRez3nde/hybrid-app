package com.rezende.matchmaking_service.repositories;


import com.rezende.matchmaking_service.dto.ActiveDriverDTO;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class DriverRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String GEO_KEY = "driver_locations";
    private static final String HASH_KEY_PREFIX = "driver:details:";
    private static final long CACHE_TTL_MINUTES = 5L;

    public DriverRedisRepository(final RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Salva ou atualiza os detalhes completos de um motorista ativo e a sua localização,
     * usando a estrutura de dados Hash do Redis. Também define um tempo de expiração.
     * @param driver DTO com os dados do motorista.
     */
    public void saveOrUpdate(final ActiveDriverDTO driver) {
        final String hashKey = HASH_KEY_PREFIX + driver.id();

        final Map<String, String> driverData = Map.of(
                "rating", String.valueOf(driver.rating()),
                "vehicleType", driver.vehicleType()
        );
        redisTemplate.opsForHash().putAll(hashKey, driverData);
        redisTemplate.expire(hashKey, CACHE_TTL_MINUTES, TimeUnit.MINUTES);

        redisTemplate.opsForGeo().add(
                GEO_KEY,
                new Point(driver.longitude(), driver.latitude()),
                driver.id()
        );
        redisTemplate.expire(GEO_KEY, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Remove um motorista do cache de motoristas ativos.
     * Chamado quando o motorista fica offline.
     * @param driverId O ID do motorista a ser removido.
     */
    public void delete(final String driverId) {
        redisTemplate.delete(HASH_KEY_PREFIX + driverId);
        redisTemplate.opsForGeo().remove(GEO_KEY, driverId);
    }

    /**
     * Calcula a distância em metros entre um motorista e um ponto geográfico.
     *
     * @param driverId O ID do motorista (membro existente no GeoSet).
     * @param passengerLocation O ponto geográfico do passageiro.
     * @return A distância em metros.
     */
    public int getDistanceInMeters(final String driverId, final Point passengerLocation) {

        String passengerTempKey = "passenger:location:" + UUID.randomUUID().toString();

        try {
            redisTemplate.opsForGeo().add(GEO_KEY, passengerLocation, passengerTempKey);
            Distance distance = redisTemplate.opsForGeo().distance(
                    GEO_KEY,
                    driverId,
                    passengerTempKey,
                    RedisGeoCommands.DistanceUnit.METERS
            );
            return (distance != null) ? (int) distance.getValue() : Integer.MAX_VALUE;

        } finally {
            redisTemplate.opsForGeo().remove(GEO_KEY, passengerTempKey);
        }
    }

    /**
     * Encontra os IDs de todos os motoristas ativos dentro de um raio especifico.
     * @param center O ponto central da busca.
     * @param radiusInMeters O raio da busca em metros.
     * @return Uma lista de IDs dos motoristas encontrados.
     */
    public List<String> findNearbyDriverIds(final Point center, final double radiusInMeters) {

        final Circle circle = new Circle(center, new Distance(radiusInMeters, RedisGeoCommands.DistanceUnit.METERS));

        final GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResult = redisTemplate.opsForGeo().radius(GEO_KEY, circle);

        if (geoResult == null) return List.of();

        return geoResult.getContent().stream()
                .map(result -> (String) result.getContent().getName())
                .toList();
    }

    /**
     * Obtém os detalhes de um motorista especifico a partir do seu Hash no Redis.
     * @param driverId O ID do motorista.
     * @return Um DTO com os detalhes do motorista.
     */
    public ActiveDriverDTO findById(final String driverId) {
        return (ActiveDriverDTO) redisTemplate.opsForValue().get(HASH_KEY_PREFIX + driverId);
    }

    /**
     * Busca os detalhes completos de múltiplos motoristas a partir dos seus IDs.
     * @param driverIds A lista de IDs dos motoristas.
     * @return Uma lista de ActiveDriverDTO com os detalhes.
     */
    public List<ActiveDriverDTO> findAllByIds(final List<String> driverIds) {
        return driverIds.stream()
                .map(this::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza apenas a avaliação (rating) de um motorista existente no cache do Redis.
     *
     * @param driverId O ID do motorista a ser atualizado.
     * @param newRating A nova avaliacao media do motorista.
     */
    public void updateRating(final String driverId, final double newRating) {
        String hashKey = HASH_KEY_PREFIX + driverId;
        redisTemplate.opsForHash().put(hashKey, "rating", String.valueOf(newRating));
    }

    public void updateLocation(final String driverId, final double latitude, final double longitude) {
        redisTemplate.opsForGeo().add(
                GEO_KEY,
                new Point(longitude, latitude),
                driverId
        );
    }

    public Point getLocation(final String driverId) {
        final List<Point> locations = redisTemplate.opsForGeo().position(GEO_KEY, driverId);
        return (locations != null && !locations.isEmpty()) ? locations.getFirst() : null;
    }
}
