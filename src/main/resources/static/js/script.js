window.addEventListener("scroll",()=>{

const navbar=document.querySelector(".navbar");

if(window.scrollY>40){

navbar.style.background="#111827";

}else{

navbar.style.background="#0f172a";

}

});