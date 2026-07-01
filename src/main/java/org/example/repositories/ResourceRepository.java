package org.example.repositories;

import org.example.domain.resources.Resource;

import java.util.List;

public interface ResourceRepository {
    void addResource(Resource resource);

    Resource findResourceByName(String name);

    List<Resource> getResources();
}
