package mom.wii.itemcustomization.util;

import net.minecraft.util.Identifier;

import java.util.HashSet;

public class IdentifierIndex {
    public HashSet<Identifier> set;

    public IdentifierIndex() {
        set = new HashSet<>();
    }

    public boolean add(Identifier identifier) {
        return set.add(identifier);
    }

    public boolean remove(Identifier identifier) {
        return set.remove(identifier);
    }

    public boolean contains(Identifier identifier) {
        return set.contains(identifier);
    }
}
