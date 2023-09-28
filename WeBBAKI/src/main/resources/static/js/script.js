$(document).ready(function () {

    //create hover-effect for the report nav element
    $('#dropdown-div-report').hover(function() {
            $("#dropdown-div-report").addClass("show");
            $("#dropdown-ul-report").addClass("show");
            $("#dropdown-a-report").attr("aria-expanded","true");
        },
        function() {
            $("#dropdown-div-report").removeClass("show");
            $("#dropdown-ul-report").removeClass("show");
            $("#dropdown-a-report").attr("aria-expanded","false");
        });

    //create hover-effect for the threatmatrix nav element
    $('#dropdown-div-matrix').hover(function() {
            $("#dropdown-div-matrix").addClass("show");
            $("#dropdown-ul-matrix").addClass("show");
            $("#dropdown-a-matrix").attr("aria-expanded","true");
        },
        function() {
            $("#dropdown-div-matrix").removeClass("show");
            $("#dropdown-ul-matrix").removeClass("show");
            $("#dropdown-a-matrix").attr("aria-expanded","false");
        });

    //create hover-effect for the admin nav element
    $('#dropdown-div-admin').hover(function() {
            $("#dropdown-div-admin").addClass("show");
            $("#dropdown-ul-admin").addClass("show");
            $("#dropdown-a-admin").attr("aria-expanded","true");
        },
        function() {
            $("#dropdown-div-admin").removeClass("show");
            $("#dropdown-ul-admin").removeClass("show");
            $("#dropdown-a-admin").attr("aria-expanded","false");
        });

});

