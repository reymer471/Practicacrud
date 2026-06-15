package edu.pucmm.eict;

import edu.pucmm.eict.ormjpa.controladores.CarritoControlador;
import edu.pucmm.eict.ormjpa.entidades.Usuario;
import edu.pucmm.eict.ormjpa.servicios.UsuarioServices;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    public static void main(String[] args) {

        // 1. Crear el usuario Admin por defecto
        if (UsuarioServices.getInstancia().find("admin") == null) {
            UsuarioServices.getInstancia().crear(new Usuario("admin", "Administrador", "admin"));
        }

        // 2. Iniciar Javalin 7 con Thymeleaf
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/publico");
            // En Javalin 7 se requiere registrar el renderizador así:
            config.fileRenderer(new JavalinThymeleaf());
        }).start(7000);

        // 3. Filtro de Seguridad
        app.before("/admin/*", ctx -> {
            Usuario usuario = ctx.sessionAttribute("usuarioLogueado");
            if (usuario == null) {
                ctx.redirect("/login");
            }
        });

        // 4. Rutas Públicas
        app.get("/", CarritoControlador::listarProductos);
        app.post("/carrito/agregar", CarritoControlador::agregarAlCarrito);
        app.get("/carrito", CarritoControlador::verCarrito);
        app.post("/carrito/procesar", CarritoControlador::procesarCompra);
        app.get("/carrito/limpiar", CarritoControlador::limpiarCarrito);

        // 5. Rutas de Autenticación
        app.get("/login", ctx -> ctx.render("/templates/login.html"));
        app.post("/login", ctx -> {
            String user = ctx.formParam("usuario");
            String pass = ctx.formParam("password");
            Usuario u = UsuarioServices.getInstancia().find(user);
            if (u != null && u.getPassword().equals(pass)) {
                ctx.sessionAttribute("usuarioLogueado", u);
                ctx.redirect("/admin/productos");
            } else {
                ctx.redirect("/login");
            }
        });

        app.get("/logout", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.redirect("/");
        });
    }
}