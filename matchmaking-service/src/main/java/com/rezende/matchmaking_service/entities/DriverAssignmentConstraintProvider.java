package com.rezende.matchmaking_service.entities;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import com.rezende.matchmaking_service.repositories.DriverRedisRepository;
import org.jspecify.annotations.NonNull;

public class DriverAssignmentConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint @NonNull [] defineConstraints(@NonNull ConstraintFactory constraintFactory) {
        return new Constraint[] {
                mustAssignADriver(constraintFactory),
                minimizePickupDistance(constraintFactory),
                maximizeDriverRating(constraintFactory)
        };
    }

    /**
     * REGRA DURA: Garante que cada corrida tenha um motorista atribuido.
     * Uma solução sem um motorista atribuido e invalida.
     */
    private Constraint mustAssignADriver(final ConstraintFactory factory) {
        return factory.forEach(RideRequest.class)
                .filter(ride -> ride.getAssignedDriver() == null)
                .penalizeLong(HardSoftScore.ONE_HARD)
                .asConstraint("Assign a driver");
    }

    /**
     * REGRA LEVE: minimiza a distância entre a localização atual do motorista
     * e o ponto de coleta do passageiro.
     */
    public Constraint minimizePickupDistance(final ConstraintFactory factory) {
        return factory.forEach(RideRequest.class)
                .join(DriverRedisRepository.class)
                .filter((ride, repo) -> ride.getAssignedDriver() != null)
                .penalizeLong(HardSoftScore.ONE_SOFT,
                    (ride, repo) -> repo.getDistanceInMeters(
                            ride.getAssignedDriver().getId(),
                            ride.getPickupLocation())
                ).asConstraint("Minimize collection distance");
    }

    /**
     * REGRA LEVE: Recompensa a escolha de motoristas com avaliacoes mais altas.
     * Quanto maior a avaliacao, maior a recompensa na pontuacao "soft".
     *
     * @param factory A fabrica de constraints fornecida pelo Timefold.
     * @return A constraint de maximizacao da avaliacao.
     */
    private Constraint maximizeDriverRating(final ConstraintFactory factory) {
        return factory.forEach(RideRequest.class)
                .filter(ride -> ride.getAssignedDriver() != null)
                .reward(HardSoftScore.ONE_SOFT,
                        ride -> {
                            double rating = ride.getAssignedDriver().getRating();
                            return (int) (rating * 100);
                        }
                ).asConstraint("Maximizar avaliação do motorista");
    }
}
