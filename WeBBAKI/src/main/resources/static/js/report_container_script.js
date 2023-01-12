function onChange(element){
    //get old url and add the new snaPid from selection element
    let oldUrl = $(location).attr("href");
    let mainUrl = oldUrl.slice(0, oldUrl.lastIndexOf("/")+1);
    let newUrl = mainUrl + element.value;
    //set to the new url
    $(location).attr("href", newUrl);
};