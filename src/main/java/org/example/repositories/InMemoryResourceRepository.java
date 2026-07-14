package org.example.repositories;

import org.example.domain.resources.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class InMemoryResourceRepository implements ResourceRepository {
    private final List<Resource> resources;
    private static final HashSet<String> NAMES_UNIQUE = new HashSet<>();

    public InMemoryResourceRepository() {
        resources = new ArrayList<>();
    }

    public void addResource(Resource resource) {
        if (NAMES_UNIQUE.contains(resource.getName())) {
            throw new IllegalArgumentException("Desk name already exists: " + resource.getName());
        }
        resources.add(resource);
        NAMES_UNIQUE.add(resource.getName());
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
