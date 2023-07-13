function updateTokenRemainingTime() {
    fetch('/user/remainingTime') // initiate http get request
        .then(response => response.text())
        .then(data => {
            // Update the HTML element with the received token remaining time
            var tokenRemainingTimeElement = document.getElementById('tokenRemainingTime');
            tokenRemainingTimeElement.innerHTML = data;
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function keepRemainderUpdated() {
    // Call the function to update the token remaining time initially and periodically
    updateTokenRemainingTime();
    setInterval(updateTokenRemainingTime, 1000); // Update every second
}
