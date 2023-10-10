let localidadList = []; // Guarda una lista con todas las localidades según la provincia seleccionada

// Cuando el usuario selecciona una provincia
async function onProvinciaSelect()
{
    const select = this.event.target; // Ontenemos el <select> padre
    const value = select.value; // Obtenemos la id de la provincia seleccionada

    // Resetear valores
    localidadList = []; // Limpiar lista de localidades
    document.querySelector("input#localidadNombre").value = ""; // Vaciar input de localidad
    document.querySelector("input#localidadNombre").removeAttribute("disabled"); // Habilitar input
    document.querySelector("input#localidad").value = ""; // Vaciar input que almacena el id de localidad

    // Enviar peticion al controlador
    const api = await fetch("/perfil/domicilios/getLocalidades?provinciaId=" + value, {method: "POST"}); // Enviamos una petición para que nos devuelva una lista con todas las localidades segun la provincia
    if(api.status === 200) // Peticion correcta
    {
        const response = await api.json();
        localidadList = response; // Llenamos la lista de localidades con la lista devuelva por el controlador
    }
}

// Autocomplete - cuando el usuario busca una localidad
$(document).ready(function()
{
    $("input#localidadNombre").autocomplete(
    {
        source: function(req, res) // Funcion para obtener la lista de localidades
        {
            const term = req.term.toLowerCase(); // "term" es el texto que introduce el usuario para buscar una localidad

            const filteredList = localidadList.filter(item => { return item.nombre.toLowerCase().includes(term)}); // Filtramos las localidades que coincidan con "term"
            res(filteredList.map(localidad => // Devolvemos como resultado un mapeo con todas las localidades filtradas
            {
                return {
                    label: localidad.nombre, // El texto que se muestra al usuario en los resultados de la busqueda
                    value: localidad // La variable que enviará a "select" cuando se seleccione un item
                }
            }));
        },
        select: function(event, ui) // Cuando el usuario selecciona una localidad
        {
            const localidad = ui.item.value; // Obtenemos los datos de la localidad

            event.preventDefault(); // Evita que autocomplete rellene el input
            $("input#localidadNombre").val(`${localidad.nombre}`); // Cambiamos el texto del input
            $("input#localidad").val(localidad.id); // Asignamos la id de la localidad seleccionada
        }
    });
});


// Cuando el usuario presiona un <button> para archivar un domicilio
function onDeleteAddress()
{
    const btn = this.event.target; // Obtener el button que disparo el evento 
    const domicilioId = btn.getAttribute("domicilio-id"); // Obtener el ID del domicilio que se quiere eliminar
    if(!domicilioId) return; // Si no tiene un domicilio-id, detener la ejecucion

    // Mostrar alerta
    Swal.fire(
    {
        title: "Eliminar domicilio",
        text: "¿Seguro que quieres eliminar este domicilio?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Eliminar",
        cancelButtonText: "Cancelar"
    }).then(async result =>
    {
        if(result.isConfirmed) // Eliminar domicilio
        {
            fetch("/perfil/domicilios/eliminar/" + domicilioId, {method: "DELETE"}) // Enviar una peticion de delete al controlador
                .then(response =>
                {
                    if(response.status == 200) // El controlador respondio con un ok
                    {
                        Swal.fire("Domicilio eliminado!", "El domicilio fue eliminado correctamente. La página se recargará.", "success")
                            .then(() => { window.location.reload(); }); // Recargar la página cuando el usuario salga del swal
                    }
                    else // El controlador respondio con problemas
                    {
                        Swal.fire(
                            {
                            icon: "error",
                            title: "Ocurrió un problema",
                            text: "Ocurrió un problema al eliminar este domicilio. Intenta recargando la página."
                        });
                    }
                }
            );
        }
    })
}


// Añadir/editar el valor de un parametro en la URL
function setURLParam(name, value) 
{
    let params = new URLSearchParams(window.location.search);
    params.set(name, value);
    window.location.search = params.toString();
}

// Quitar parametro de la URL
function removeURLParam(name) 
{
    let params = new URLSearchParams(window.location.search);
    params.delete(name);
    window.location.search = params.toString();
}

// Añadir/editar un parámetro y eliminar otro parámetro de la URL
function toggleURLParams(setParam, value, deleteParam)
{
    let params = new URLSearchParams(window.location.search);
    params.set(setParam, value);
    params.delete(deleteParam);
    window.location.search = params.toString();
}