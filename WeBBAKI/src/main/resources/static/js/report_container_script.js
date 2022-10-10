function onChange(element){
    //get old url and add the new snapid from selection element
    let oldUrl = $(location).attr("href");
    let mainUrl = oldUrl.slice(0, oldUrl.length - 1);
    let newUrl = mainUrl + element.value;
    $(location).attr("href", newUrl);
};