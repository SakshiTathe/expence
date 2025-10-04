const mongoose = require('mongoose');
const videoSchema = new mongoose.Schema({
    title: { type: String, required: true },
    description: { type: String },
    videoFile: { type: String, required: true },
    createdAt:{type:Date,default:Date.now},
    videoFileId: { type: mongoose.Schema.Types.ObjectId, required: true }, // Reference to GridFS file
    uploadedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },  // Teacher who uploaded
    likes: { type: Number, default: 0 },
    //likes: { type: Array, default: [] },
    comments: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Comment' }]

});
module.exports = mongoose.model('Video', videoSchema);

