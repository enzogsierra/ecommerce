// Cuando el usuario hace click en el <button> "Añadir categoria"
function onAddCategoryClick()
{
    Swal.fire(
    {
        title: "Añadir categoría",
        input: "text",
        showCancelButton: true,
        confirmButtonText: "Añadir",
        cancelButtonText: "Cancelar",
        showLoaderOnConfirm: true,
        preConfirm: async (nombre) => 
        {
            const headers = {"Content-Type": "application/json"}; // Enviar en formato json
            const properties = JSON.stringify({"nombre": nombre}); // Propiedades de la entidad

            const api = await fetch("/admin/categorias/anadir", {method: "POST", body: properties, headers: headers}); // Hacer una peticion al controlador para que cree la entidad

            if(api.status != 200) // Si el controlador respondio con errores, mostrar un mensaje al usuario
            {
                const response = await api.json();
                const msg = Object.values(response);
                Swal.showValidationMessage(msg);
            }
        },
        allowOutsideClick: () => !Swal.isLoading()
    }).then((result) => 
    {
        if(result.isConfirmed) {
            Swal.fire("Categoría creada", "La categoría fue creda correctamente. La página se recargará.", "success").then(() => window.location.reload());
        }
    });
}

// Cuando el usuario hace click en el <button> "Añadir subcategoria"
function onAddSubcategoryClick()
{
    const btn = this.event.target;
    const categoriaId = btn.getAttribute("category-id");
    if(!categoriaId) return;

    Swal.fire(
    {
        title: "Añadir subcategoría",
        input: "text",
        showCancelButton: true,
        confirmButtonText: "Añadir",
        cancelButtonText: "Cancelar",
        showLoaderOnConfirm: true,
        preConfirm: async (nombre) => 
        {
            const headers = {"Content-Type": "application/json"}; // Enviar en formato json
            const properties = // Propiedades de la entidad
            {
                "categoria": {"id": categoriaId},
                "nombre": nombre
            }; 

            const api = await fetch("/admin/subcategorias/anadir", {method: "POST", body: JSON.stringify(properties), headers: headers}); // Hacer una peticion al controlador para que cree la entidad

            if(api.status == 200) {} // OK
            else if(api.status == 400) // Si el controlador respondio con un badRequest, mostrar los mensajes de validacion
            {
                const response = await api.json();
                const msg = Object.values(response);
                Swal.showValidationMessage(msg);
            }
            else // El controlador respondio con otro error
            {
                Swal.showValidationMessage("Ocurrió un problema al crear la subcategoría. Intenta recargando la página.");
            }

        },
        allowOutsideClick: () => !Swal.isLoading()
    }).then((result) => 
    {
        if(result.isConfirmed) {
            Swal.fire("Subcategoría creada", "La subcategoría fue creda correctamente. La página se recargará.", "success").then(() => window.location.reload());
        }
    });
}


// Cuando el usuario presiona un <button> para eliminar una categoría
function onDeleteCategory()
{
    const btn = this.event.target; // Obtener el button que disparo el evento 
    const categoryId = btn.getAttribute("category-id"); // Obtener el ID de la categoría que se quiere eliminar
    if(!categoryId) return; // Si no tiene un category-id, detener la ejecucion

    // Mostrar alerta
    Swal.fire(
    {
        title: "¿Estás seguro?",
        text: "Esta categoría se eliminará junto con todas sus subcategorías. Esta acción es irreversible.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Eliminar",
        cancelButtonText: "Cancelar"
    }).then(async result =>
    {
        if(result.isConfirmed) // Eliminar categoria
        {
            const api = await fetch("/admin/categorias/eliminar/" + categoryId, {method: "DELETE"}); // Enviar una peticion delete al controlador
            if(api.status == 200) // El controlador respondio con un ok
            {
                Swal.fire("Categoría eliminada!", "La categoría fue eliminada correctamente. La página se recargará.", "success")
                    .then(() => { window.location.reload(); }); // Recargar la página cuando el usuario salga del swal
            }
            else // El controlador respondio con problemas
            {
                Swal.fire(
                {
                    icon: "error",
                    title: "Ocurrió un problema",
                    text: "Hubo un problema al eliminar la categoría. Intenta recargando la página."
                });
            }
        }
    });
}

// Cuando el usuario presiona un <button> para eliminar una subcategoria
function onDeleteSubcategory()
{
    const btn = this.event.target; // Obtener el button que disparo el evento 
    const subcategoryId = btn.getAttribute("subcategory-id"); // Obtener el ID de la subcategoría que se quiere eliminar
    if(!subcategoryId) return; // Si no tiene un category-id, detener la ejecucion

    // Mostrar alerta
    Swal.fire(
    {
        title: "¿Estás seguro?",
        text: "Esta subcategoría se eliminará de lista. Esta acción es irreversible.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Eliminar",
        cancelButtonText: "Cancelar"
    }).then(async result =>
    {
        if(result.isConfirmed) // Eliminar categoría
        {
            const api = await fetch("/admin/subcategorias/eliminar/" + subcategoryId, {method: "DELETE"}); // Enviar una peticion delete al controlador
            if(api.status == 200) // El controlador respondio con un ok
            {
                Swal.fire("Subcategoría eliminada!", "La subcategoría fue eliminada correctamente. La página se recargará.", "success")
                    .then(() => { window.location.reload(); }); // Recargar la página cuando el usuario salga del swal
            }
            else // El controlador respondio con problemas
            {
                Swal.fire(
                {
                    icon: "error",
                    title: "Ocurrió un problema",
                    text: "Hubo un problema al eliminar la subcategoría. Intenta recargando la página."
                });
            }
        }
    });
}