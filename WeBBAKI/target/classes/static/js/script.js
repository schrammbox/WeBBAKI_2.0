$(document).ready(function () {
    //create hover-effect for the threatmatrix nav element
    $('#dropdown-div').hover(function() {
            $("#dropdown-div").addClass("show");
            $("#dropdown-ul").addClass("show");
            $("#dropdown-a").attr("aria-expanded","true");
        },
        function() {
            $("#dropdown-div").removeClass("show");
            $("#dropdown-ul").removeClass("show");
            $("#dropdown-a").attr("aria-expanded","false");
        });
});