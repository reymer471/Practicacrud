import controladores.AdminControlador;
import controladores.CarritoControlador;
import entidades.Usuario;
import servicios.UsuarioServices;

import io.javalin.Javalin;
import io.javalin.rendering.FileRenderer;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // Crear admin por defecto
        if (UsuarioServices.getInstancia().find("admin") == null) {
            UsuarioServices.getInstancia()
                    .crear(new Usuario("admin", "Administrador", "admin"));
        }

        // Configuración de Thymeleaf
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        // Crear aplicación Javalin 7
        Javalin.create(config -> {

            // Archivos estáticos
            // config.staticFiles.add("/publico");

            // FileRenderer con Thymeleaf
            config.fileRenderer((filePath, model, ctx) -> {
                Context thymeContext = new Context();
                if (model != null) {
                    Map<String, Object> variables = new HashMap<>(model);
                    thymeContext.setVariables(variables);
                }
                return engine.process(filePath, thymeContext);
            });

            // ── Proteger rutas /admin/* ──────────────────────────────
            config.routes.before("/admin/*", ctx -> {
                Usuario usuario = ctx.sessionAttribute("usuarioLogueado");
                if (usuario == null) {
                    ctx.redirect("/login");
                    ctx.skipRemainingHandlers();
                }
            });

            // ── Tienda y carrito ─────────────────────────────────────
            config.routes.get("/",
                    CarritoControlador::listarProductos);

            config.routes.post("/carrito/agregar",
                    CarritoControlador::agregarAlCarrito);

            config.routes.get("/carrito",
                    CarritoControlador::verCarrito);

            config.routes.get("/carrito/eliminar/{id}",
                    CarritoControlador::eliminarDelCarrito);

            config.routes.post("/carrito/procesar",
                    CarritoControlador::procesarCompra);

            config.routes.get("/carrito/limpiar",
                    CarritoControlador::limpiarCarrito);

            // ── Login ────────────────────────────────────────────────
            config.routes.get("/login",
                    ctx -> ctx.render("login", Map.of()));

            config.routes.post("/login", ctx -> {
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

            // ── Logout ───────────────────────────────────────────────
            config.routes.get("/logout", ctx -> {
                ctx.req().getSession().invalidate();
                ctx.redirect("/");
            });

            // ── Administración ───────────────────────────────────────
            config.routes.get("/admin/productos",
                    AdminControlador::listarProductosAdmin);

            config.routes.post("/admin/productos/crear",
                    AdminControlador::crearProducto);

            config.routes.get("/admin/productos/eliminar/{id}",
                    AdminControlador::eliminarProducto);

            config.routes.get("/admin/ventas",
                    AdminControlador::listarVentas);

        }).start(7000);
    }
}