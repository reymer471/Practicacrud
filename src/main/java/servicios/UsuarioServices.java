package servicios;
import entidades.Usuario;

public class UsuarioServices extends GestionDb<Usuario> {
    private static UsuarioServices instancia;
    private UsuarioServices() { super(Usuario.class); }
    public static UsuarioServices getInstancia() {
        if (instancia == null) { instancia = new UsuarioServices(); }
        return instancia;
    }
}