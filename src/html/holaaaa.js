
var time = 30; // time in millie seconds
const image = document.getElementById("edit-save");
    const url = '/ClepnidCompartirPantalla'
    const options = {
        method: "GET"
    }
//function
async function load_pic() { 
    let response = await fetch(url, options)
    if (response.status === 200) {
        const imageBlob = await response.blob()
        const imageObjectURL = URL.createObjectURL(imageBlob);
        image.src = imageObjectURL
    }
}
function changeImage() {
    load_pic()
    setTimeout('changeImage()', time);
}

window.onload = changeImage;