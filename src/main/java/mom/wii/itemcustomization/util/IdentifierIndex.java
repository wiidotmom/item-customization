package mom.wii.itemcustomization.util;

import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IdentifierIndex {
    public HashSet<Identifier> identifiers;
    public HashSet<String> namespaces;
    public Identifier id;

    public IdentifierIndex(Identifier id) {
        this.id = id;
        identifiers = new HashSet<>();
        namespaces = new HashSet<>();
    }

    public boolean add(Identifier identifier) {
        namespaces.add(identifier.getNamespace());
        return identifiers.add(identifier);
    }

    public List<Identifier> getIdentifiersOfNamespace(String namespace) {
        return identifiers.stream().filter(id -> id.getNamespace().equals(namespace)).collect(Collectors.toList());
    }

    private Identifier getTruncatedPath(String namespace, String path, String queryPath) {
        String remaining = path.substring(queryPath.length());
        int nextSlash = remaining.indexOf('/');
        String truncatedPath = nextSlash == -1 ?
                remaining : remaining.substring(0, nextSlash + 1);
        return Identifier.of(namespace, queryPath + truncatedPath);
    }

    public static Optional<Identifier> getParentDir(Identifier identifier) {
        Optional<Identifier> parentDir = Optional.empty();
        String path = identifier.getPath();
        String namespace = identifier.getNamespace();
        if (!path.isEmpty()) {
            if (!path.contains("/"))
                parentDir = Optional.of(Identifier.of(namespace, ""));
            else {
                String parentStr = path.substring(0, path.lastIndexOf('/'));
                if (!parentStr.contains("/")) parentDir = Optional.of(Identifier.of(namespace, ""));
                else parentDir = Optional.of(Identifier.of(namespace, parentStr.substring(0, parentStr.lastIndexOf('/') + 1)));
            }
        }
        return parentDir;
    }

    public boolean isValidIdentifier(Identifier identifier) {
        return containsNamespace(identifier.getNamespace()) && identifiers.stream().anyMatch(id -> id.getNamespace().equals(identifier.getNamespace()) && id.getPath().startsWith(identifier.getPath()));
    }

    public List<Identifier> getIdentifiersOfNamespaceAndPath(String namespace, String path) {
        Set<Identifier> filteredIdentifiers = identifiers.stream()
                .filter(id -> id.getNamespace().equals(namespace))
                .flatMap(id -> {
                    String idPath = id.getPath();
                    if (path.isEmpty()) {
                        if (!idPath.contains("/"))
                            return Stream.of(id);
                        return Stream.of(getTruncatedPath(namespace, idPath, path));
                    }
                    if (idPath.startsWith(path))
                        return Stream.of(getTruncatedPath(namespace, idPath, path));
                    if (path.equals(idPath + "/"))
                        return Stream.of(id);
                    return Stream.empty();
                })
                .collect(Collectors.toUnmodifiableSet());
        ArrayList<Identifier> sorted = new ArrayList<>(filteredIdentifiers.stream().filter(id -> id.getPath().endsWith("/")).sorted(Identifier::compareTo).toList());
        sorted.addAll(filteredIdentifiers.stream().filter(id -> !id.getPath().endsWith("/")).sorted(Identifier::compareTo).toList());
        return sorted;
    }

    public boolean remove(Identifier identifier) {
        boolean result = identifiers.remove(identifier);
        if (identifiers.stream().noneMatch(id -> id.getNamespace().equals(identifier.getNamespace()))) {
            namespaces.remove(identifier.getNamespace());
        }
        return result;
    }

    public boolean contains(Identifier identifier) {
        return identifiers.contains(identifier);
    }

    public boolean containsNamespace(String namespace) {
        return namespaces.contains(namespace);
    }

    public boolean isEmpty() {
        return identifiers.isEmpty();
    }

    public void clear() {
        identifiers.clear();
        namespaces.clear();
    }
}
