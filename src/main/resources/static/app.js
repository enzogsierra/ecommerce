
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
            fetch("/productos/eliminar/" + productId, {method: "DELETE"}) // Enviar una peticion de delete al controlador
                .then(response =>
                {
                    if(response.status == 200) // El controlador respondio con un ok
                    {
                        Swal.fire("Producto archivao!", "El producto fue archivado correctamente. La página se reiniciará.", "success")
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
    })
}