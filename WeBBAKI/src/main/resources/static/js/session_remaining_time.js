function updateSessionRemainingTime() {
    console.log("update Remaining Time got called.");
    fetch('/user/remainingTime') // initiate http get request to access
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
    // Call the function to update the session remaining time initially and periodically
    updateSessionRemainingTime();
    setInterval(updateSessionRemainingTime, 1000); // Update every second
}
