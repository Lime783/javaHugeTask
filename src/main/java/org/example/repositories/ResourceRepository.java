package org.example.repositories;

import org.example.domain.resources.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {
    void addResource(Resource resource);
    Optional<Resource> findResourceByName(String name);
    List<Resource> findAllResources();
}
