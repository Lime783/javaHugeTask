package org.example.repositories;

import org.example.domain.resources.Resource;

import java.util.List;

public class InMemoryResourceRepository implements ResourceRepository {
    List<Resource> resources;

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public Resource findResourceByName(String name) {
        for (Resource resource : resources) {
            if (resource.getName().equals(name)) {
                return resource;
            }
        }
        throw new IllegalArgumentException("Resource with name " + name + " not found");
    }

    public List<Resource> getResources() {
        return resources;
    }
}
