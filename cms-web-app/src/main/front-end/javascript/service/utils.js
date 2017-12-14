import compileExpression from './filterx';

function isFunction(functionToCheck) {
    let getType = {};
    return functionToCheck && getType.toString.call(functionToCheck) === '[object Function]';
}


const FILTER_FUNCTIONS = {};

const applyPropsFilter = (props, filter)=>{
    if( !filter )
        return true;
    let filterFunction = FILTER_FUNCTIONS[filter] || ( FILTER_FUNCTIONS[filter] = compileExpression(filter) );
    return filterFunction(props);
};

export {isFunction, applyPropsFilter};