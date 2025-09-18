package com.rezende.matchmaking_service.repositories;


import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActiveDriverRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY = "active-drivers";

    public ActiveDriverRepository(final RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void updateLocation(final String driverId, final double latitude, final double longitude) {
        redisTemplate.opsForGeo().add(
                KEY,
                new Point(longitude, latitude),
                driverId
        );
    }

    public Point getLocation(final String driverId) {
        final List<Point> locations = redisTemplate.opsForGeo().position(KEY, driverId);
        return (locations != null && !locations.isEmpty()) ? locations.getFirst() : null;
    }
}
