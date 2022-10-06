$(document).ready(function () {
    for (let i = 0; i < 35; i++) {
        $('#' + 'selectProb' + i).on('change', function () {
            calc()
        });
        $('#' + 'selectImp' + i).on('change', function () {
            calc()
        });
    }

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


function calc() {

    function createBackgroundColor(id, i) {

        switch (id) {
            case 'noRisk' + i:
                return document.getElementById('noRisk' + i).style.backgroundColor = "green";
            case 'smallRisk' + i:
                return document.getElementById("smallRisk" + i).style.backgroundColor = "yellow";
            case 'increasedRisk' + i:
                return document.getElementById("increasedRisk" + i).style.backgroundColor = "darkyellow";
            case 'highRisk' + i:
                return document.getElementById("highRisk" + i).style.backgroundColor = "orange";
            case 'veryHighRisk' + i:
                return document.getElementById("veryHighRisk" + i).style.backgroundColor = "red";
        }
    }

    for (let i = 0; i < 35; i++) {
        var probValue = $('#selectProb' + i).val();
        var impValue = $('#selectImp' + i).val();
        let probNum, impNum, result = 0;
        switch (probValue) {
            case 'nie':
                probNum = 0
                break;
            case 'selten':
                probNum = 1
                break;
            case 'mittel':
                probNum = 2
                break;
            case 'haeufig':
                probNum = 3
                break;
            case 'sehr haeufig':
                probNum = 4
                break;
        }
        switch (impValue) {
            case 'keine':
                impNum = 1
                break;
            case 'geringe':
                impNum = 2
                break;
            case 'hohe':
                impNum = 3
                break;
            case 'existenzielle':
                impNum = 4
                break;
        }
        result = probNum * impNum;
        $("#noRisk" + i).hide();
        $("#smallRisk" + i).hide();
        $("#increasedRisk" + i).hide();
        $("#highRisk" + i).hide();
        $("#veryHighRisk" + i).hide();
        switch (true) {
            case (result === 0):
                createBackgroundColor("noRisk", i)
                $("#" + 'noRisk' + i).show()
                break;
            case (result < 5):
                createBackgroundColor("smallRisk", i)
                $("#" + 'smallRisk' + i).show()
                break;
            case (result < 10):
                createBackgroundColor("increasedRisk", i)
                $("#" + 'increasedRisk' + i).show()
                break;
            case (result === 12):
                createBackgroundColor("highRisk", i)
                $("#" + 'highRisk' + i).show()
                break;
            case (result === 16):
                createBackgroundColor("veryHighRisk", i)
                $("#" + 'veryHighRisk' + i).show()
                break;
        }
    }
}

