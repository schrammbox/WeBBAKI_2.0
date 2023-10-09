var remainingTime = 0;

function onTokenRemainingTimeLoad() {
    let element = document.querySelector("#tokenRemainingTime");
    if (element) {
        remainingTime = parseInt(element.dataset.time);
        element.innerText = new Date(remainingTime * 1000).toISOString().slice(11, 19);
        setInterval(function () {

            remainingTime--;
            if (remainingTime < 0) {
                location.reload();
            } else {
                element.innerText = new Date(remainingTime * 1000).toISOString().slice(11, 19);
            }
        }, 1000);
    }
}