package com.rezende.taxi_service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Location {

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point coordinates;

    public static Location from(final double latitude, final double longitude) {

        final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        final Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        final Location location = new Location();
        location.setCoordinates(point);
        return location;
    }
}
