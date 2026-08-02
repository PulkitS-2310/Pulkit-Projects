const BASE_URL = 'https://your-api-url.com'; // Replace with your actual API base URL

export const api = {
  // Friends
  getFriends: async (username) => {
    const response = await fetch(`${BASE_URL}/users/${username}/friends`);
    return await response.json();
  },

  addFriend: async (username, friendUsername) => {
    const response = await fetch(`${BASE_URL}/users/${username}/friends`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ friendUsername }),
    });
    return await response.json();
  },

  removeFriend: async (username, exFriendUsername) => {
    const response = await fetch(`${BASE_URL}/users/${username}/friends`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ exFriendUsername }),
    });
    return await response.json();
  },

  // Followers
  getFollowers: async (username) => {
    const response = await fetch(`${BASE_URL}/users/${username}/followers`);
    return await response.json();
  },

  removeFollower: async (username, followerUsername) => {
    const response = await fetch(`${BASE_URL}/users/${username}/followers`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ followerUsername }),
    });
    return await response.json();
  },

  // Following
  getFollowing: async (username) => {
    const response = await fetch(`${BASE_URL}/users/${username}/following`);
    return await response.json();
  },

  startFollowing: async (username, followingUsername) => {
    const response = await fetch(`${BASE_URL}/users/${username}/following`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ followingUsername }),
    });
    return await response.json();
  },

  unfollow: async (username, followingUsername) => {
    const response = await fetch(`${BASE_URL}/users/${username}/following`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ followingUsername }),
    });
    return await response.json();
  },
};
