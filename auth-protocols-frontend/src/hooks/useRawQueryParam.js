import { useLocation } from 'react-router-dom';

const useRawQueryParam = (param) => {
    const location = useLocation();
    const query = location.search.substring(1);
    const params = query.split('&');
    
    for (let i = 0; i < params.length; i++) {
      const indexOfEqual = params[i].indexOf('=');
      if (indexOfEqual === -1) {
        continue;
      }
  
      let key = params[i].substring(0, indexOfEqual);
      let value = params[i].substring(indexOfEqual + 1);
  
      if (key === param) {
        return value;
      }
    }
    return null;
  };

export default useRawQueryParam;