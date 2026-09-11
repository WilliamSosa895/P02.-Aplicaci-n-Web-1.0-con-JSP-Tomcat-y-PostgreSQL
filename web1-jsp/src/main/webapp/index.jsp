<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>PR10 - Gestion de practicas profesionales (Web 1.0)</title>
    <style>
        body { font-family: system-ui, Arial, sans-serif; margin: 2rem auto; max-width: 900px; padding: 0 1rem; color: #1a1a1a; }
        h1 { font-size: 1.4rem; } h2 { font-size: 1.1rem; margin-top: 2rem; border-bottom: 1px solid #ccc; padding-bottom: .3rem; }
        table { border-collapse: collapse; width: 100%; margin-top: .5rem; }
        th, td { border: 1px solid #ccc; padding: .4rem .6rem; text-align: left; font-size: .95rem; }
        th { background: #f2f2f2; }
        form { margin-top: .5rem; display: grid; gap: .5rem; max-width: 520px; }
        label { display: grid; gap: .2rem; font-size: .95rem; }
        input, select, button { padding: .4rem; font-size: .95rem; }
        button { width: fit-content; cursor: pointer; }
        .aviso { background: #fdeaea; border: 1px solid #e0a3a3; padding: .5rem .7rem; border-radius: 4px; }
        .pie { margin-top: 2rem; font-size: .85rem; color: #555; }
    </style>
</head>
<body>
    <h1>PR10 - Gestion de practicas profesionales</h1>
    <p>Incremento Web 1.0 (JSP, Servlet y JDBC sobre PostgreSQL). Datos ficticios.</p>

    <c:if test="${not empty error}">
        <p class="aviso"><c:out value="${error}"/></p>
    </c:if>

    <h2>1. Registrar organizacion</h2>
    <form method="post" action="catalog">
        <label>Nombre de la organizacion
            <input name="name" minlength="3" required placeholder="Al menos 3 caracteres">
        </label>
        <label>Sector (opcional)
            <input name="sector" placeholder="Ej. Consultoria TI">
        </label>
        <button type="submit">Guardar organizacion</button>
    </form>

    <h2>2. Organizaciones registradas</h2>
    <table>
        <thead><tr><th>ID</th><th>Nombre</th><th>Sector</th></tr></thead>
        <tbody>
            <c:forEach items="${organizaciones}" var="o">
                <tr>
                    <td><c:out value="${o.id}"/></td>
                    <td><c:out value="${o.nombre}"/></td>
                    <td><c:out value="${o.sector}"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty organizaciones}">
                <tr><td colspan="3">Sin organizaciones registradas.</td></tr>
            </c:if>
        </tbody>
    </table>

    <h2>3. Postular estudiante a una organizacion</h2>
    <form method="post" action="postulacion">
        <label>Organizacion
            <select name="organizacionId" required>
                <c:forEach items="${organizaciones}" var="o">
                    <option value="${o.id}"><c:out value="${o.nombre}"/></option>
                </c:forEach>
            </select>
        </label>
        <label>Estudiante
            <select name="estudianteId" required>
                <c:forEach items="${estudiantes}" var="e">
                    <option value="${e.id}"><c:out value="${e.nombre}"/></option>
                </c:forEach>
            </select>
        </label>
        <label>Titulo de la practica
            <input name="titulo" minlength="3" required placeholder="Ej. Practica de desarrollo web">
        </label>
        <button type="submit">Registrar postulacion</button>
    </form>

    <h2>4. Catalogo de postulaciones</h2>
    <table>
        <thead><tr><th>ID</th><th>Organizacion</th><th>Estudiante</th><th>Titulo</th><th>Estado</th></tr></thead>
        <tbody>
            <c:forEach items="${postulaciones}" var="p">
                <tr>
                    <td><c:out value="${p.id}"/></td>
                    <td><c:out value="${p.organizacion}"/></td>
                    <td><c:out value="${p.estudiante}"/></td>
                    <td><c:out value="${p.titulo}"/></td>
                    <td><c:out value="${p.estado}"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty postulaciones}">
                <tr><td colspan="5">Sin postulaciones registradas.</td></tr>
            </c:if>
        </tbody>
    </table>

    <p class="pie"><a href="health">Verificar salud del servicio</a></p>
</body>
</html>
