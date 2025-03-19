import { createGlobalStyle } from "styled-components";


const GlobalStyle = createGlobalStyle`

@media (max-width: 500px){
    .PageContainer {
        flex-direction: column;
        justify-content: center;
    }
}

`

export default GlobalStyle