const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    userId: { type: String,required: true,unique:true  },
    password: { type: String,required: true},
    name: { type: String, required: true },
    role: { type: String, required: true },
    class: { type: String},
    email: { type: String},
    aboutUs: { type: String, },  // For teacher's profile
    profileImage: { type: String},  // URL or file path for the profile image
    uploadedVideos: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Video' }],
    likedVideos: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Video' }],//Videos user liked
    watchLater: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Video' }]// Videos user added to watch later
});

module.exports = mongoose.model('User', userSchema);

