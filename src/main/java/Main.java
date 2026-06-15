package edu.pucmm.eict.ormjpa;

import edu.pucmm.eict.ormjpa.controladores.AdminControlador;
import edu.pucmm.eict.ormjpa.controladores.CarritoControlador;
import edu.pucmm.eict.ormjpa.entidades.Usuario;
import edu.pucmm.eict.ormjpa.servicios.UsuarioServices;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    public static void main(String[] args) {

        // 1. Crear el usuario Admin por defecto exigido por la práctica
        if (UsuarioServices.getInstancia().find("admin") == null) {
            UsuarioServices.getInstancia().crear(new Usuario("admin", "Administrador", "admin")); // [cite: 22]
        }

        // 2. Iniciar Javalin 7 con soporte para plantillas Thymeleaf
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/publico");
            config.fileRenderer(new JavalinThymeleaf());
        }).start(7000);

        // 3. Control de Seguridad con Filtros interceptores
        app.before("/admin/*", ctx -> {
            Usuario usuario = ctx.sessionAttribute("usuarioLogueado");
            if (usuario == null) {
                ctx.redirect("/login");
            }
        });

        // 4. Endpoints de la Tienda Pública
        app.get("/", CarritoControlador::listarProductos); // [cite: 25]
        app.post("/carrito/agregar", CarritoControlador::agregarAlCarrito); //
        app.get("/carrito", CarritoControlador::verCarrito); // [cite: 28]
        app.post("/carrito/procesar", CarritoControlador::procesarCompra); // [cite: 30]
        app.get("/carrito/limpiar", CarritoControlador::limpiarCarrito);

        // 5. Autenticación de Sesión
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

        // 6. Endpoints Privados de Administración
        app.get("/admin/productos", AdminControlador::listarProductosAdmin); // [cite: 21]
        app.post("/admin/productos/crear", AdminControlador::crearProducto);
        app.get("/admin/productos/eliminar/{id}", AdminControlador::eliminarProducto);
        app.get("/admin/ventas", AdminControlador::listarVentas); // [cite: 31]
    }
}