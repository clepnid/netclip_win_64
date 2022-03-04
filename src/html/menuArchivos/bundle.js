document.addEventListener("keypress", logKey);

function logKey(e) {
  var foco = document.getElementById("buscar-en-foco");
  if (foco) {
    if (e.keyCode === 13) {
      const inputValue = document.getElementById("js-search").value;
      window.location.assign("/searchFile/" + inputValue);
    }
  } else {
    foco = document.getElementById("p-focus");
    if (foco) {
      if (e.keyCode === 13) {
        window.location.assign(foco.textContent);
      }
    }
  }
}
