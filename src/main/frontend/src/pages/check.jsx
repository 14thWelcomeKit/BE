import React, { useState, useEffect } from "react";  // useState import 추가
import Header from "../components/Header";
import CheckBoard from "../components/Board";
import styled from "styled-components";
import { IoMdClose } from "react-icons/io";
import axiosInstance from "../axiosInstance";
import axios from "axios";
import media from "styled-media-query"


const PageContainer = styled.div`
    width : 100%;
    height : 59.38rem;
    padding : 2.25rem 3.44rem;
    display: flex;
    flex-direction: row;
    background: var(--Bold-Black, #1C1B1A);
    box-sizing: border-box;
    gap: 10.69rem;

    ${media.lessThan("medium")`
    display: flex;
    width: 100%;
    height: 52.75rem;
    flex-direction: column;
    align-items: center;
    gap: 3rem;
    box-sizing: border-box;
    padding : 3rem 1rem;
  `}
`

const TextContainer = styled.div`
    display: flex;
    width: 32.8125rem;
    height : 18.12rem;
    flex-direction: column;
    margin-top : 12.25rem;
    gap : 2.5rem;

    ${media.lessThan("medium")`
        width : 24.375rem;
        height : 27.88rem;
        display: flex;
        padding: 0rem 1rem;
        align-items: center;
        gap: 2.5rem;
        box-sizing: border-box;
        justify-content: flex-end;
        margin-top : 0rem;
  `}
`

const ButtonContainer = styled.div`
    width : 26.75rem;
    height: 3.25rem;
    gap : 2rem;
    display: flex;
    flex-direction: row;

    ${media.lessThan("medium")`
        flex-direction: column; 
        justify-content: center;
        align-items: center;
        width : 12.37rem;
        height: 13.75rem;
  `}
`

const TitleText = styled.h1`
    font-family: Montserrat;
    font-size: 3.7rem;
    font-style: normal;
    font-weight: 700;
    line-height: 140%; /* 5.6rem */
    letter-spacing: -0.1rem;
    color: #FFFF;
    margin: 0;
`

const MiddleText = styled.h1`
    font-family: Pretendard;
    font-size: 1.5rem;
    font-style: normal;
    font-weight: 600;
    line-height: 140%; /* 2.1rem */
    letter-spacing: -0.0375rem;
    color: #FFFF;
    margin: 0;


    ${media.lessThan("medium")`
        font-size: 1.25rem;
  `}
`

const QrButton = styled.div`
    display: flex;
    width: 12.375rem;
    height: 3.25rem;
    justify-content: center;
    align-items: center;
    background-color: #FFFF;
    border-radius: 3.125rem;
    font-family: Pretendard;
    font-size: 1.125rem;
    font-style: normal;
    font-weight: 500;
    line-height: 140%; /* 1.575rem */
    letter-spacing: -0.02813rem;
    color : #FF7710;
    cursor: pointer;
`

const CheckButton = styled.div`
    display: flex;
    width: 12.375rem;
    height: 3.25rem;
    justify-content: center;
    align-items: center;
    background-color: #FFFF;
    border-radius: 3.125rem;
    font-family: Pretendard;
    font-size: 1.125rem;
    font-style: normal;
    font-weight: 500;
    line-height: 140%; /* 1.575rem */
    letter-spacing: -0.02813rem;
    color : #FF7710;
    cursor: pointer;

    ${media.greaterThan("medium")`
        display : none;
  `}
`

const QRModal = styled.div`
    display: flex;
    flex-direction: column;
    width: 35.9375rem;
    height: 35.9375rem;
    padding: 1.125rem 1rem;
    justify-content: center;
    align-items: center;
    background-color: #FFFF;
    position: fixed;  
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 1000;  
    border-radius: 1rem;
`

const ModalOverlay = styled.div`
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);  
    z-index: 999;  
`

const Close = styled(IoMdClose)`
    margin-left: auto;
    cursor: pointer;
`



export default function Check() {
    const [isModalOpen, setIsModalOpen] = useState(false); 
    const [qrImage, setQrImage] = useState(null);
    const [boarddata , setBoarddata] = useState(null);

    
    useEffect(() => {
        if (isModalOpen) {
            const fetchQr = async () => {
                try {
                    const response = await axios.get('https://welcomekitbe.lion.it.kr/api/attendance/generate-qr', {
                        responseType: "blob",
                    });
                    const imageUrl = URL.createObjectURL(response.data);
                    setQrImage(imageUrl);
                } catch (error) {
                    console.error('Error fetching data:', error);
                }
            };

            fetchQr();
        }
    }, [isModalOpen]); 

    const openModal = () => {
        setIsModalOpen(true);
        fetchBoarddata();};
    const closeModal = () => {
        setIsModalOpen(false);
        setQrImage(null); // 모달 닫을 때 QR 이미지 초기화
    }; 


        const fetchBoarddata = async () => {
            try {
                const response = await axiosInstance.get('/attendance/today/attendance'
              );
              console.log(response.data);
              setBoarddata(response.data);
            } catch (error) {
              console.error('Error fetching data:', error);
            }
          }

    return (
        <>
            <Header />
            <PageContainer>
                <TextContainer>
                    <TitleText>QR 출석체크</TitleText>
                    <MiddleText>
                        QR코드로 출석체크를 진행해주세요.<br/>
                        출석체크는 모바일에서만 가능합니다.
                    </MiddleText>
                    <ButtonContainer>
                        <QrButton onClick={openModal}>QR 체크 진행</QrButton>
                        <CheckButton>출석하기</CheckButton>
                        <QrButton>출석부 최신화</QrButton>
                    </ButtonContainer>
                </TextContainer>
                {boarddata && boarddata.length > 0 ? <CheckBoard memberdata={boarddata} /> : null}
            </PageContainer>

            {isModalOpen && (
                <>
                    <ModalOverlay onClick={closeModal} />
                    <QRModal>
                        <Close onClick={closeModal} size = {24}/>
                        {qrImage ? <img src={qrImage} alt="QR Code" style={{ width: "35rem", height: "35rem" }}/> : <p>Loading...</p>}
                    </QRModal>
                </>
            )}
        </>
    );
}
