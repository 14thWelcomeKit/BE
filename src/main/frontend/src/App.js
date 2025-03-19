import RouterComponent from './router';
import './App.css';
import GlobalStyle from './GlobalStyles';
import { AuthProvider } from './AuthContext';


function App() {
  return (
  <>
    <AuthProvider>
      <GlobalStyle />
      <RouterComponent />
    </AuthProvider>
  </>);
}

export default App;
