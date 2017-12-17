//create globa event emitter instance
import {EventEmitter} from "fbemitter";

const globalEmitter = new EventEmitter();
window.globalEmitter = globalEmitter;

export {globalEmitter}