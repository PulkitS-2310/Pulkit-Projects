http://coms-3090-029.class.las.iastate.edu:8080

Examples of format for HTTP requests at the bottom of the file

Online API here:
http://coms-3090-029.class.las.iastate.edu:8080/swagger-ui/index.html#/

Javadoc documentation for the backend is in the Documents/Javadoc Backend folder in the main branch. Link:
https://git.las.iastate.edu/cs309/2025spring/3_rasel_7/-/tree/main/Documents/Javadoc%20Backend?ref_type=heads


Image Functionality:
--------------------

/upload
POST: Input is a MultipartFile, the image then gets transferred to the backend for later retrieval.

/images/{id}
GET: Input is the ID of an image, returns an array of bytes that contain the image data. 

- Takes the image from the frontend and uploads it to the DB



Notifications Functionality:
----------------------------

/notifications/{username}
GET: Returns a Map<String, Object> where "Chats" is the key for a Map<String, String> with the keys being friend usernames or chat IDs and the values being the most recent message, and "Posts" is the key for a Map<String, String> with the keys being the username of the person they follow who made a post and the value being the title of the post.




Search/Post Functionality:
--------------------------

/search/{username}
GET: Searches for users whose usernames contain the given query (case-insensitive).

/search/post/{title}
GET: Searches for posts that contain the given title (case-insensitive).

/users/{username}/post/{id}
GET: returns a post with the id matching the {id} provided. {username} seems unnecessary
DELETE: deletes the post with matching {id}

/allPosts
GET: returns a List<Post> of every post in the repository

/users/{username}/posts
GET: returns all posts made by a user with {username}

/users/{username}/post
POST: creates a post using the provided Post object in the request body.
needs to have a title, or it will not work. automatically associates the username and timeUploaded when sent.

user data and format



Timeline Functionality:
-----------------------

/users/{username}/timeline
GET: returns the timeline of the given user

/users/{username}/refresh
GET: refreshes then returns the timeline of the given user

/allTimelines
GET: returns every timeline object, used for testing.


Chat Functionality:
-------------------

/getDMs
GET: returns a List<DM> that is the entire DM repository.

/chat/{username}
Websocket endpoint where the user cannot message anyone except directly. The commands used are as follows:

	@{friend} {message} : sends a DM to the person with the username {friend}. omits the @{friend} portion of the message. should be prepended by the frontend when in the correct chat.

	@{friend} /getChats : retrieves all DMs between {username} and {friend}. does not send anything but retrieves the chat history. should be sent automatically when opening the chat, cannot be done by the backend with how it is currently set up.

	@{friend} POST:{postid} : sends a post using the post's id. sends the json data of the post to be stored in the chat, in the format username: {<data here>}


/allGroups
GET: returns a list of all groups

/group/{id}
GET: input is the ID of the group to be returned.
DELETE: deletes the group with that ID

/group
GET: input is a Map<String, String> where the keys "groupId" OR "groupName" and "creator" are the keys for the group name and the creator of the group which together find the group object in the repository and return its string representation.
PUT: input is a Map<String, String> where the keys "groupName" and "creator" are the keys for the group name and the creator of the group which together find the group object in the repository and update it. The keys for the updated variables are "newGroupName" (String), "newCreator" (String), "nicknames" (Map<String, String>), "usernames" (ArrayList<String>). 
POST: input is a Map<String, String> where the keys "groupName" and "creator" are the keys for the group name and the creator of the group to be created.
DELETE: input is a Map<String, String> where the keys "groupId" OR "groupName" and "creator" are the keys used to identify which group to delete. 


/groupChat/{username}
websocket endpoint where the user can message any group they are in, and all group chats are sent to the same endpoint but sorted using the groupId. The commands used are as follows:

	@{id} /getChats : retrieves all messages in the group with groupId {id}. does not send anything but retrieves the group chat history. should be sent automatically when opening the chat, cannot be done by the backend with how it is currently set up.

	@{id} {message} : sends a message to the groupchat with the ID {id}. omits the @{id} portion of the message. should be prepended by the frontend when in the correct chat.

	@{id} POST:{postid} : sends a post using the post's id. sends the json data of the post to be stored in the chat, in the format username: {<data here>}





Profile Functionality:
----------------------

/users/{username}/profile
GET: Returns this user's profile
PUT: Updates this user's profile. Request body is a Map<String, Object> that uses the keys "name", "username", "bio" (String), "posts" (ArrayList<Object>), "pfpID" (int), "chats" (ArrayList<Object>). The values for the keys are the object that is going to be updating/replacing the value of that variable. 
POST: Creates a profile for a user. Request body is a Map<String, Object> that uses the keys "name" (String), "pfpID" (int), "bio" (String), "posts" (ArrayList<Object>), "chats" (ArrayList<Object>).

/profiles
GET: Returns a list of all profiles

this is now old and we use another one instead
/users/{username}/profile/newPost
POST: returns the post object provided after adding it to the given user's profile. the post is provided in json format with the key "post" in the request body.




User Functionality:
-------------------


/users
GET: Returns all users

/signup
POST: adds user to database. Body - json User object

/users/{username}
GET: Returns user with username
PUT: Updates user with username. Body - Map<String, Object> which is just the format for users where "friends", "followers", "following"  are of the type (ArrayList<String>). Must have correct password.
Can update following and remove followers and friends but not add them.
DELETE: Deletes user with username. Body - Map<String, String> where "password" is the key for the correct user password.

/users/{username}/friends
GET: Returns list of User objects that are friends of the user with username
POST: Adds two users as friends with eachother making them follow eachother, with one user being username. Body - Map<String, String> where "friendUsername" and "password" are the needed keys for the information.

/users/{username}/removeFriend
POST: Removes two users as friends with eachother making them unfollow eachother, with one user being username. Body - Map<String, String> where "friendUsername" and "password" are the needed keys for the information.

/users/{username}/followers
GET: Returns list of User objects that are followers of the user with username
POST: Makes followerUsername start following username. Body - Map<String, String> where "followerUsername" and "password" are the needed keys for the information. The password is the password for the user with followerUsername.

/users/{username}/removeFollower
DELETE: Removes follower of username. Body - Map<String, String> where "followerUsername" and "password" are the needed keys for the information. The password is the password for the user with username.

/users/{username}/following
GET: Returns list of User objects that are following the user with username

/users/{username}/follow
POST: Makes username start following followingUsername. Body - Map<String, String> where "followingUsername" and "password" are the needed keys for the information.

/users/{username}/unfollow
POST: Makes username unfollow followingUsername. Body - Map<String, String> where "followingUsername" is the needed key for the information.

/login
POST: checks if the provided password for username or email is correct. Body - Map<String, String> info, where "password" is the key for the password and "username" for username or "email" is the key for the full email address. A successful login returns the string "Password correct"

/user/change-password
POST: checks if the password is correct then updates it to the newPassword. Body - Map<String, String> info, where "password" is the key for the current password, "newPassword" is the key for the new password, and "username" is the key for username.


IMAGES:

/images/upload
POST: Allows users to upload an image file. The metadata of the image (name, size, and upload time) is stored in the database.
The method accepts a MultipartFile object from the request body, which contains the uploaded image file.

A new image entity is created, and its fields are populated:
imageName: Set to the original filename of the uploaded file.
imageSize: Set to the size of the file in bytes.
uploadTime: Set to the current timestamp using LocalDateTime.now(); formatted in "HH:mm 'T' MM-dd-yyyy"

/images/all
GET: Fetches a list of all images stored in the database.
The method calls imageRepository.findAll() to retrieve all records from the database.

/images/{id}
GET: Fetches a specific image's metadata by its unique ID.
It uses imageRepository.findById(id) to retrieve the image record with the given ID.

/images/{id}
DELETE: Deletes a specific image's metadata from the database based on its unique ID.

Image format for postman requests:
{"id": 506, "image_name": "WoT wallpaper.jpg", "image_size": "200392", "upload_time": "2025-04-02T14:48:43.229028"}
{"id": 507, "image_name": "Screenshot 2025-03-30 225208.png", "image_size": "155064", "upload_time": "2025-04-02T14:56:32.6712937"}
{"id": 515, "image_name": "Kia K5 gt.avif", "image_size": "112560", "upload_time": "16:1104-02-2025"}
{"id": 516, "image_name": "WoT wallpaper", "image_size": "", "upload_time": "16:1104-02-2025"}



User data and format for sending postman requests:

{"name":"John","username":"jdog357","email":"john@somemail.com","password":"password123"}
{"name":"George","username":"gman","email":"george@neopets.com","password":"123456"}
{"name":"Paul","username":"pmccartney","email":"pm@beatles.org","password":"idiedin1967"}
{"name":"Ringo","username":"ringostarr","email":"rgo@cybersmith.com","password":"hamandcheese"}
{"username":"Test","email":"Test","password":"Test"}
{"name":"Testing","username":"Testing","email":"Testing","password":"Testing"}


Password format and correct unhashed passwords
/login
{"username":"jdog357", "password":"password123"}
{"email":"pm@beatles.org", "password":"idiedin1967"}
{"username": "gman", "password": "123456" }
{"username":"ringostarr", "password":"beans"}
{"username": "A", "password": "B"}
{"username":"Test", "password":"Test"}
{"username":"Test2", "password":"Test2"}
{"username":"TestUserName", "password":"TestPassword"}
{"username":"Testing", "password":"Testing"}



/user/change-password
{"password":"password123","newPassword":"password456","username":"jdog357"}

/users/jdog357 (PUT)
{"name":"John","username":"jdog357","email":"john@somemail.com","password":"password123","friends":["ringostarr"],"following":["gman","pmccartney","ringostarr"],"followers":["ringostarr"]}

/users/Test (DELETE)
{"password":"Test"}

/users/jdog357/friends (POST or DELETE)
{"friendUsername":"ringostarr","password":"password123"}

/users/jdog357/following (POST or DELETE)
{"followingUsername":"ringostarr","password":"password123"}

Should not be used normally, makes someone else follow the current user and requires the other 
/users/jdog357/followers (POST)
{"followerUsername":"ringostarr","password":"beans"}

/users/jdog357/followers (DELETE)
{"followerUsername":"ringostarr","password":"password123"}


Search functionality:
---------------------

/users/{username}/post
GET:

hashtags
{"id": "1", "tag": "#test"}
{"id": "2", "tag": "#lol"}
{"id": "3", "tag": "#lfg"}