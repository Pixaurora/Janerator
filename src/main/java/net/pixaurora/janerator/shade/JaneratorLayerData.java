package net.pixaurora.janerator.shade;

public interface JaneratorLayerData {
    public static final JaneratorLayerData DEFAULT = new JaneratorLayerData() {
        @Override
        public boolean generateStructures() {
            return true;
        }
    };

    boolean generateStructures();
}
