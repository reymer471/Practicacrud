package servicios;
import entidades.VentasProductos;

public class VentasServices extends GestionDb<VentasProductos> {
    private static VentasServices instancia;
    private VentasServices() { super(VentasProductos.class); }
    public static VentasServices getInstancia() {
        if (instancia == null) { instancia = new VentasServices(); }
        return instancia;
    }
}