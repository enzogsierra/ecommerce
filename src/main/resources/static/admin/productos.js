document.addEventListener("DOMContentLoaded", () =>
{
    // Mostrar un thumbnail cuando se sube/edita la imagen de un producto
    const inputs = document.querySelectorAll("input[data-img-thumb]"); // Seleccionar todos los inputs que tengan este atributo
    inputs.forEach(thumb => thumb.addEventListener("change", function() // Iterar sobre cada elemento
    {
        const id = this.getAttribute("data-img-thumb"); // Obtener el <img id> al que hace referencia el input
        const src = URL.createObjectURL(this.files[0]); // Obtener la URL donde se almacena la imagen antes de ser subida

        const img = document.querySelector(id); // Seleccionar el <img> que muestra el thumbnail
        img.classList.remove("d-none"); 
        img.src = src; // Darle la URL obtenida
    }));
});


// Cuando el usuario presiona un <button> para archivar un producto
function onDeleteProduct()
{
    const btn = this.event.target; // Obtener el button que disparo el evento 
    const productId = btn.getAttribute("product-id"); // Obtener el ID del producto que se quiere archivar
    if(!productId) return; // Si no tiene un product-id, detener la ejecucion

    // Mostrar alerta
    Swal.fire(
    {
        title: "Archivar producto",
        text: "Este producto se archivará y no se mostrará a los usuarios. Puedes volver a activar este producto cuando desees.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Archivar",
        cancelButtonText: "Cancelar"
    }).then(async result =>
    {
        if(result.isConfirmed) // Archivar producto
        {
            fetch("/admin/productos/archivar/" + productId, {method: "DELETE"}) // Enviar una peticion de delete al controlador
                .then(response =>
                {
                    if(response.status == 200) // El controlador respondio con un ok
                    {
                        Swal.fire("Producto archivado!", "El producto fue archivado correctamente. La página se reiniciará.", "success")
                            .then(() => { window.location.reload(); }); // Recargar la página cuando el usuario salga del swal
                    }
                    else // El controlador respondio con problemas
                    {
                        Swal.fire(
                            {
                            icon: "error",
                            title: "Ocurrió un problema",
                            text: "Hubo un problema al archivar el producto. Intenta recargando la página."
                        });
                    }
                }
            );
        }
    });
}

// Cuando el usuario selecciona una categoría - mostrar las subcategorias de la categoria seleccionada
async function onCategorySelect()
{
    const select = this.event.target; // Ontenemos el <select> padre
    const categoryId = select.value; // Obtenemos el ID de la categoría seleccionada

    const subcategories = document.querySelectorAll("select#subcategoria[category-id]"); // Obtenemos el <select> que muestra las subcategorías de la categoria seleccionada
    subcategories.forEach((subcategory => // Iterar sobre las listas de subcategorias
    {
        if(subcategory.getAttribute("category-id") == categoryId) // Si el [category-id] de la lista coincide con la categoria seleccionada
        {
            subcategory.classList.remove("d-none"); // Mostrar <select>
            subcategory.removeAttribute("disabled"); // Quitar el atributo disabled - de esta forma este campo se envía al controlador
        }
        else // Si no coincide
        {
            subcategory.classList.add("d-none"); // Ocultar select
            subcategory.setAttribute("disabled", "disabled"); // Añadir el atribute disabled, así no se envia al controlador
        }
    }));
}