package org.n52.project.enforce.geoquest.api.impl.geoquest;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GeoquestQuestsRepository extends JpaRepository<GeoquestQuests, UUID> {

}
