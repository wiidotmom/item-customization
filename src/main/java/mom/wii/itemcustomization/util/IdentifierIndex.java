package mom.wii.itemcustomization.util;

import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

    public boolean isEmpty() {
        return identifiers.isEmpty();
    }

    public void clear() {
        identifiers.clear();
        namespaces.clear();
    }
}
