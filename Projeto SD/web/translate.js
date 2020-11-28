function traduzir(){
    var lang = "pt";
    var text = document.getElementById("text").value;

    $.get("https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20191213T143806Z.e6c5a711cbe0f2ed.345f08eff1795b0e749195b99eeaf421908c9ecc" +
        "&text="+text+
        "&lang="+lang, function(result){
        document.getElementById("text").innerHTML=result.text;
    })

}
function detetar(){
    var text = document.getElementById("text").value;

    $.get("https://translate.yandex.net/api/v1.5/tr.json/detect?key=trnsl.1.1.20191213T143806Z.e6c5a711cbe0f2ed.345f08eff1795b0e749195b99eeaf421908c9ecc" +
        "&text="+text
        , function(result){
        document.getElementById("lingua").innerHTML=result.lang;
    })

}