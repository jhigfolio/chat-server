// MILESTONES:
// 1. setup files and makes sure they all link up
// 2. create the wireframe of html with divs and ids necessary to append content to them
// 3. connect to the server via websockets
// 4. send request to join room
// 5. recieve data from room
// 6. add ability to send message to server
// 7. generate messages form server on html
// 8. updates messages with each message that is sent

window.onload = function() {

    // variables to store room info
    let currentRoom;
    let currentUsr;
    let historyArr = [];
    // let allUsers = [];

    // variables to reference HTML elements
    let displayMessage = document.getElementById("displayMessages");
    let roomTitle = document.getElementById("roomName");
    let roomTabs = document.getElementById("room-tabs");

    // initialize socket
    let socket = new WebSocket("ws://" + location.host);

    // helper function to detectRoomChange
    let detectRoomChange = function (newRoom, newUser) {

        let userChange = currentUsr != newUser;
        let roomChange = currentRoom != newRoom;

        if (userChange || roomChange) {
            socket.close();
            socket = new WebSocket("ws://" + location.host);
            currentRoom = newRoom;
            currentUsr = newUser;
            return true;
        } 

        return false;
    }

    let changeRoom = function() {

        // updates room name
        let roomTitleText = document.createTextNode(`room name: ${currentRoom}`);
        while (roomName.firstChild) {
            roomTitle.removeChild(roomTitle.firstChild);
        }
        roomTitle.appendChild(roomTitleText);

        // empties the div where previous messages have been displayed
        while (displayMessage.firstChild) {
            displayMessage.removeChild(displayMessage.firstChild);
        }

        let req = "join " + currentRoom;

        socket.onopen = function () {
            socket.send(req);
        }
    }

    let updateHistoryTags = function() {
        let roomInfo = {
            room: currentRoom,
            usr: currentUsr
        }

        historyArr.push(roomInfo);

        let tab = this.document.createElement("li");
        tab.classList.add("tab");
        tab.classList.add("col");
        tab.classList.add("s3");

        let a = this.document.createElement("a");
        a.dataset.index = historyArr.length - 1;
        a.classList.add("yellow-text");
        a.classList.add("text-lighten-1");
        a.href = `#${historyArr[historyArr.length - 1].room}`;

        a.addEventListener("click", (e) => {
            let newUser = historyArr[a.dataset.index].usr;
            let newRoom = historyArr[a.dataset.index].room;
            if(detectRoomChange(newRoom, newUser)) {
                changeRoom();
                displayMessages();
            }    
        });

        let aTxt = this.document.createTextNode(historyArr[historyArr.length - 1].room);
        a.appendChild(aTxt);
        tab.appendChild(a);
        roomTabs.appendChild(tab);
    }

    let displayMessages = function () {
        socket.onmessage = function(e) {
            console.log(e.data)
            // create elements
            let br = document.createElement("br");
            let messageDiv = document.createElement("div");
            let usernameDiv = document.createElement("div");
            
            // parse data from server into a JSON obj
            let messageData = JSON.parse(e.data);

            // if (!allUsers.includes(messageData.user)) {
            //     allUsers.push(messageData.user);
            // }

            // let userList = document.getElementById("all-users");
            // userList.innerHTML = "";

            // allUsers.forEach(() => {
            //     let userListDiv = createElement("div");
            //     let msgContent = document.createTextNode(this);

            // });
            
            // assign appropriate classes to divs
            // break out into seperate function
            if (currentUsr === messageData.user) {
                messageDiv.classList.add("userMessage");
                br.classList.add("clear-float");
                // messageDiv.classList.add("right");
                let msgContent = document.createTextNode(messageData.message);
                messageDiv.appendChild(msgContent);
                displayMessage.appendChild(messageDiv);
                displayMessage.appendChild(br);
            } else {
                messageDiv.classList.add("message");
                usernameDiv.classList.add("userTag");

                // insert data into the divs as text nodes
                let msgContent = document.createTextNode(messageData.message);
                let usrTag = document.createTextNode(messageData.user);
                messageDiv.appendChild(msgContent);
                usernameDiv.appendChild(usrTag);

                // append divs to parent node
                displayMessage.appendChild(usernameDiv);  
                displayMessage.appendChild(messageDiv);
                displayMessage.appendChild(br);
            }

             // scrolls to the bottom of div, the latest entry
            displayMessage.scrollTop = displayMessage.scrollHeight;
        }; 
    }
    
    this.document.getElementById("joinRoom").addEventListener("click", (e) => {
        e.preventDefault();

        let newUser = this.document.getElementById("username").value;
        let newRoom = this.document.getElementById("room").value;

        if(detectRoomChange(newRoom, newUser)) {
            changeRoom();
            updateHistoryTags();
            displayMessages();
        }
    });

    this.document.getElementById("joinRoomMobile").addEventListener("click", (e) => {
        e.preventDefault();

        let newUser = this.document.getElementById("username").value;
        let newRoom = this.document.getElementById("room").value;

        if(detectRoomChange(newRoom, newUser)) {
            changeRoom();
            updateHistoryTags();
            displayMessages();
        }
    });

    this.document.getElementById("sendUserMessage").addEventListener("click", (e) => {
        e.preventDefault();
       
        // grab and send message to server
        let message = this.document.getElementById("userMessage").value;
        socket.send(currentUsr + " " + message);

        displayMessages();

        // empty input field for new message to be typed in without user having to delete
        document.getElementById("userMessage").value = "";
    });

    M.AutoInit();

}


