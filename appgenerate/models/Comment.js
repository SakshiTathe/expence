const mongoose = require('mongoose');
const questionSchema = new mongoose.Schema({
    question: { type: String, required: true },
    author: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    video: { type: mongoose.Schema.Types.ObjectId, ref: 'Video' },  // Related to a video
    answers: [{
        content: { type: String },
        author: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
        timestamp: { type: Date, default: Date.now }
    }],
    timestamp: { type: Date, default: Date.now }
});
