import React, { useState } from "react";
import styled from "styled-components";
import { BsInstagram } from "react-icons/bs";
import { SiVelog } from "react-icons/si";
import Header from "../components/Header";

import introduce1 from "../image/introduce1.png";
import Logo from "../image/LIKELION UNIV._white@3x.png";
import Together from "../image/IMG_9678 1.png";
import Left from "../image/Left.svg";
import Right from "../image/Right.svg";
import Image1 from "../image/image 12.png";
import Image2 from "../image/image 13.png";
import Image3 from "../image/image 14.png";
import Image4 from "../image/image 15.png";
import Image5 from "../image/image 16.png";
import Image6 from "../image/image 17.png";
import Image7 from "../image/image 18.png";
import Image8 from "../image/image 19.png";
import Image9 from "../image/image 20.png";
import Image10 from "../image/image 21.png";
import Image11 from "../image/image 22.png";
import Image12 from "../image/image 11.png";
import Image13 from "../image/image 24.png";
import Image14 from "../image/image 25.png";

const breakpoints = {
  mobile: "576px",
  tablet: "768px",
  laptop: "1024px",
  desktop: "1200px",
};

const Frame = styled.div`
  background: #1c1b1a;
  width: 100%;
  margin: 0 auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const Container = styled.div`
  display: flex;
  width: 100%;
  flex-direction: column;
  align-items: center;
  gap: 320px;
  padding-top: 54px;
  max-width: 1440px;
  padding: 0 20px;
  box-sizing: border-box;

  @media (max-width: ${breakpoints.tablet}) {
    gap: 160px;
    padding: 30px 20px;
  }
`;

const Contents = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 240px;
  align-self: stretch;
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;

  @media (max-width: ${breakpoints.tablet}) {
    gap: 120px;
  }
`;

const TitleContainer = styled.div`
  display: flex;
  width: 100%;
  flex-direction: column;
  align-items: center;
  gap: 80px;

  @media (max-width: ${breakpoints.tablet}) {
    gap: 40px;
  }
`;

const TitleText = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  align-self: stretch;
`;

const Title = styled.p`
  color: #fff;
  text-align: center;
  font-family: Montserrat;
  font-size: 32px;
  font-weight: 600;
  line-height: 140%;
  letter-spacing: -0.8px;
  margin: 0;

  @media (max-width: ${breakpoints.tablet}) {
    font-size: 28px;
  }

  @media (max-width: ${breakpoints.mobile}) {
    font-size: 24px;
  }
`;

const SideTitle = styled(Title)`
  text-align: ${(props) => (props.alignRight ? "right" : "left")};

  @media (max-width: ${breakpoints.tablet}) {
    text-align: center;
  }
`;

const Explain = styled.p`
  color: #fff;
  text-align: center;
  font-family: Pretendard;
  font-size: 20px;
  font-weight: 400;
  line-height: 140%;
  letter-spacing: -0.5px;
  margin: 0;

  @media (max-width: ${breakpoints.tablet}) {
    font-size: 16px;
  }

  @media (max-width: ${breakpoints.mobile}) {
    br {
      display: none;
    }
  }
`;

const ResponsiveImage = styled.img`
  width: 100%;
  height: auto;
  max-width: ${(props) => props.maxWidth || "100%"};
  max-height: ${(props) => props.maxHeight || "auto"};
  object-fit: contain;
`;

const Start = styled.div`
  color: #fff;
  text-align: center;
  font-family: Pretendard;
  font-size: 36px;
  font-weight: 600;
  line-height: 140%;
  letter-spacing: -0.9px;
  padding: 0 20px;
  margin: 0;

  span {
    color: #ff7710;
  }

  @media (max-width: ${breakpoints.tablet}) {
    font-size: 28px;
  }

  @media (max-width: ${breakpoints.mobile}) {
    font-size: 22px;
  }
`;

const First = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 80px;
  width: 100%;

  @media (max-width: ${breakpoints.tablet}) {
    gap: 40px;
  }
`;

const SubTitle = styled.div`
  color: #fff;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  font-family: Pretendard;
  font-size: 24px;
  font-weight: 600;
  line-height: 140%;
  letter-spacing: -0.6px;
  flex-wrap: wrap;
  padding: 0 20px;
  margin: 0;

  span {
    color: #ff7710;
  }

  @media (max-width: ${breakpoints.tablet}) {
    font-size: 20px;
  }

  @media (max-width: ${breakpoints.mobile}) {
    font-size: 18px;
  }
`;

const Track = styled.div`
  display: flex;
  align-items: center;
  gap: 120px;
  align-self: stretch;
  justify-content: center;
  flex-wrap: wrap;

  @media (max-width: ${breakpoints.laptop}) {
    gap: 60px;
  }

  @media (max-width: ${breakpoints.tablet}) {
    gap: 30px;
    flex-direction: column;
  }
`;

const TrackName = styled.p`
  color: #fff;
  text-align: center;
  font-family: Pretendard;
  font-size: 48px;
  font-weight: 600;
  line-height: 140%;
  letter-spacing: -1.2px;
  margin: 0;

  @media (max-width: ${breakpoints.laptop}) {
    font-size: 40px;
  }

  @media (max-width: ${breakpoints.tablet}) {
    font-size: 32px;
  }

  @media (max-width: ${breakpoints.mobile}) {
    font-size: 28px;
  }
`;

const SmallText = styled.p`
  color: #9d9d9d;
  text-align: center;
  font-family: Pretendard;
  font-size: 16px;
  font-weight: 300;
  line-height: 140%;
  letter-spacing: -0.4px;
  margin: 0;

  @media (max-width: ${breakpoints.mobile}) {
    font-size: 14px;
  }
`;

const Second = styled.div`
  display: flex;
  width: 45vw;
  max-width: 670px;
  aspect-ratio: 1 / 1;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-shrink: 0;
  border-radius: 50%;
  background: linear-gradient(
    180deg,
    rgba(254, 88, 38, 0.5) 0%,
    rgba(0, 0, 0, 0) 47.5%
  );
  text-align: center;
  padding: 20px;
  box-sizing: border-box;

  @media (max-width: ${breakpoints.laptop}) {
    width: 45vw;
  }

  @media (max-width: ${breakpoints.tablet}) {
    width: 50vw;
  }

  @media (max-width: ${breakpoints.mobile}) {
    width: 75vw;
    max-width: 280px;
  }
`;

const SecondContainer = styled.div`
  display: flex;
  width: 90%;
  max-width: 550px;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 30px;

  @media (max-width: ${breakpoints.tablet}) {
    max-width: 280px;
    gap: 20px;
  }

  @media (max-width: ${breakpoints.mobile}) {
    max-width: 230px;
    gap: 15px;
  }
`;

const Text = styled.p`
  color: #fff;
  text-align: center;
  font-family: Pretendard;
  font-size: 20px;
  font-weight: 300;
  line-height: 140%;
  letter-spacing: -0.5px;
  margin: 0;
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;

  @media (max-width: ${breakpoints.tablet}) {
    font-size: 16px;
    max-width: 85%;
  }

  @media (max-width: ${breakpoints.mobile}) {
    font-size: 14px;
    max-width: 80%;
  }
`;

const SideText = styled(Text)`
  text-align: ${(props) => (props.alignRight ? "right" : "left")};
  white-space: pre-line;

  @media (max-width: ${breakpoints.tablet}) {
    text-align: center;
    white-space: normal;
  }
`;

const ContentsContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 48px;
  align-self: stretch;
  align-items: center;
  width: 100%;

  @media (max-width: ${breakpoints.tablet}) {
    gap: 30px;
  }
`;

const ImageContainer = styled.div`
  width: 100%;
  overflow: hidden;
  white-space: nowrap;
  display: flex;
  align-items: center;
`;

const ImageContent = styled.div`
  display: flex;
  gap: 24px;
  width: calc(100% * 2);
  animation: scroll 20s linear infinite;

  @keyframes scroll {
    from {
      transform: translateX(0);
    }
    to {
      transform: translateX(-50%);
    }
  }

  img {
    height: auto;
    width: 100%;
    max-width: 300px;
    aspect-ratio: 1/1;
    object-fit: contain;

    @media (max-width: ${breakpoints.tablet}) {
      max-width: 200px;
    }

    @media (max-width: ${breakpoints.mobile}) {
      max-width: 150px;
    }
  }
`;

const ImageClick = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 80px;
  width: 100%;

  @media (max-width: ${breakpoints.laptop}) {
    gap: 40px;
  }

  @media (max-width: ${breakpoints.tablet}) {
    gap: 20px;
  }

  img {
    object-fit: contain;

    &.arrow {
      width: 30px;
      height: auto;
      cursor: pointer;

      @media (max-width: ${breakpoints.mobile}) {
        width: 20px;
      }
    }

    &.content {
      width: 100%;
      max-width: 904px;
      height: auto;
      max-height: 468px;
    }
  }
`;

const SideImage = styled.div`
  display: flex;
  align-items: center;
  gap: 24px;
  width: 100%;

  @media (max-width: ${breakpoints.laptop}) {
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }

  @media (max-width: ${breakpoints.tablet}) {
    flex-direction: column;
    align-items: center;
  }
`;

const SideContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: ${(props) => (props.alignRight ? "flex-end" : "flex-start")};
  gap: 48px;
  width: 100%;
  @media (max-width: ${breakpoints.laptop}) {
    align-items: center;
  }
  @media (max-width: ${breakpoints.tablet}) {
    gap: 30px;
    align-items: center;
  }
`;

const SocialContainer = styled(SideImage)`
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  max-width: 900px;
  width: auto;

  @media (max-width: ${breakpoints.tablet}) {
    flex-direction: column;
    justify-content: center;
    flex-wrap: wrap;
    gap: 40px;
  }

  @media (max-width: ${breakpoints.mobile}) {
    flex-direction: column;
    gap: 30px;
  }
`;

const SocialItem = styled(SideImage)`
  @media (max-width: ${breakpoints.tablet}) {
    flex-direction: row;
    gap: 15px;
  }

  @media (max-width: ${breakpoints.mobile}) {
    gap: 10px;
  }
`;

const SocialIcon = styled.div`
  width: 92px;
  height: 92px;
  color: #ff7710;

  @media (max-width: ${breakpoints.tablet}) {
    width: 72px;
    height: 72px;
  }

  @media (max-width: ${breakpoints.mobile}) {
    width: 52px;
    height: 52px;
  }

  svg {
    width: 100%;
    height: 100%;
  }
`;

const SocialText = styled(Start)`
  @media (max-width: ${breakpoints.tablet}) {
    font-size: 22px;
  }

  @media (max-width: ${breakpoints.mobile}) {
    font-size: 18px;
  }
`;

const CAROUSEL_IMAGES = [Image9, Image10, Image11];
const SCROLL_IMAGES = [
  Image1,
  Image2,
  Image3,
  Image4,
  Image5,
  Image6,
  Image7,
  Image8,
];

export default function Introduce() {
  const [currentIndex, setCurrentIndex] = useState(0);

  const goToPrevious = () => {
    setCurrentIndex((prevIndex) =>
      prevIndex === 0 ? CAROUSEL_IMAGES.length - 1 : prevIndex - 1
    );
  };

  const goToNext = () => {
    setCurrentIndex((prevIndex) =>
      prevIndex === CAROUSEL_IMAGES.length - 1 ? 0 : prevIndex + 1
    );
  };

  const handleSocialClick = (url) => {
    window.location.href = url;
  };

  return (
    <Frame>
      <Header />
      <Container>
        <Contents>
          <TitleContainer>
            <TitleText>
              <Title>About us</Title>
              <Explain>
                2013년, 서울대학교에서 이두희 대표를 필두로 시작된
                '멋쟁이사자처럼 대학'. 현재는 국내외 121개 대학, 4천여 명이
                활동하는 <br />
                국내 최대 규모의 IT 창업 동아리로 자리매김하였습니다. "내
                아이디어를 내 손으로 실현한다"는 모토로, <br />
                누구든지 자신이 원하는 IT 서비스를 구현할 수 있도록 각종
                스터디와 네트워킹, 행사를 지원하고 있습니다.
              </Explain>
            </TitleText>
            <ResponsiveImage src={introduce1} alt="소개 이미지" />
          </TitleContainer>

          <Start>
            멋쟁이 사자처럼 대학은 13기를 맞이해,{" "}
            <span>보다 전문적인 IT 창업 동아리</span>로 새롭게 시작합니다.
          </Start>

          <First>
            <SubTitle>
              1. 개발 트랙을 &nbsp;<span>3가지</span>로 나누어 진행합니다.
            </SubTitle>
            <Track>
              <TrackName>FE(프론트엔드)</TrackName>
              <TrackName>BE(백엔드)</TrackName>
              <TrackName>FE or BE(공통)</TrackName>
            </Track>
            <SmallText>
              * 공통 트랙은 5주차 이후, FE와 BE 중 트랙 선택 필요
            </SmallText>
          </First>

          <Second>
            <SecondContainer>
              <SubTitle>
                2. 2024년보다 &nbsp;<span> 더욱 강화된 교육</span>을 제공합니다.
              </SubTitle>
              <ResponsiveImage src={Logo} alt="멋사 로고" maxWidth="441px" />
              <Text>
                멋쟁이사자처럼 대학 교육 플랫폼 TECHIT 강좌를 무상 제공합니다.
                <br />
                활동 시 선택한 트랙이 아니여도 모든 강의를 수강하실 수 있습니다.
              </Text>
            </SecondContainer>
          </Second>

          <ContentsContainer>
            <SubTitle>
              3. &nbsp;<span>각종 해커톤</span>을 통해 여러분의 코딩 실력을
              상승시킬 수 있습니다.
            </SubTitle>
            <Text>
              🦁 중앙아이디어톤(5월), 중앙해커톤(8월)을 비롯한 각종 연합해커톤을
              통해 여러 사람과 교류하며 코딩 실력도 향상시킬 수 있습니다.
            </Text>
            <ResponsiveImage
              src={Together}
              alt="함께하는 이미지"
              maxWidth="906px"
              maxHeight="468px"
            />
            <ImageContainer>
              <ImageContent imageCount={SCROLL_IMAGES.length}>
                {SCROLL_IMAGES.map((src, i) => (
                  <img key={i} src={src} alt={`logo-${i}`} />
                ))}
                {SCROLL_IMAGES.map((src, i) => (
                  <img
                    key={`duplicate-${i}`}
                    src={src}
                    alt={`logo-duplicate-${i}`}
                  />
                ))}
              </ImageContent>
            </ImageContainer>
          </ContentsContainer>

          <ContentsContainer>
            <SubTitle>
              4. 다양한 개발 경험과 능력 있는 운영진들이 &nbsp;
              <span>직접 세션</span>을 진행합니다.
            </SubTitle>
            <Text>
              🦁 총 8분의 운영진이 각 팀 프로젝트의 팀장을 맡고 정성들여 세션도
              진행합니다.
            </Text>
            <ImageClick>
              <img
                src={Left}
                onClick={goToPrevious}
                className="arrow"
                alt="이전"
              />
              <img
                src={CAROUSEL_IMAGES[currentIndex]}
                className="content"
                alt={`세션 이미지 ${currentIndex + 1}`}
              />
              <img
                src={Right}
                onClick={goToNext}
                className="arrow"
                alt="다음"
              />
            </ImageClick>
          </ContentsContainer>

          <ContentsContainer>
            <SubTitle>
              5. 2학기 &nbsp;<span>추가 트랙 구성</span>&nbsp;( NEW❗)
            </SubTitle>
            <Text>
              추가적인 트랙 선택을 통한 다양한 교육 프로그램 이수가 가능합니다.
            </Text>
            <ResponsiveImage
              src={Image12}
              alt="추가 트랙"
              maxWidth="904px"
              maxHeight="320px"
            />
          </ContentsContainer>

          <Start>
            추가적으로, 한국외대(글로벌) 멋사는 다음과 같은 <span>특별한</span>{" "}
            활동을 진행합니다.
          </Start>

          <SideImage>
            <ResponsiveImage
              src={Image13}
              alt="교육봉사"
              maxWidth="672px"
              maxHeight="336px"
            />
            <SideContainer>
              <SideTitle>풍생고 교육봉사 활동</SideTitle>
              <SideText>
                풍생고 학생들을 대상으로 코딩 멘토링 프로그램을 진행합니다.
                프론트엔드와 백엔드 파트로 나누어 진행되며, 멘토들은 학생들과
                함께 스터디를 운영하거나 프로젝트를 수행합니다. 풍생고 교육봉사
                진행 시, 봉사활동 시간이 인정되며 소정의 강사비도 지급됩니다.
              </SideText>
            </SideContainer>
          </SideImage>

          <SideImage>
            <SideContainer alignRight>
              <SideTitle alignRight>멋쟁이사자처럼 대학 연합해커톤</SideTitle>
              <SideText alignRight>
                멋사 대학에 소속되어 있는 다양한 학교와 함께 해커톤을
                진행합니다. IT 분야에 관심이 있는 학생들 간의 소통과
                커뮤니케이션을 증진시키고, 서로의 아이디어와 경험을 나눌 수 있는
                네트워킹 기회를 제공합니다.
              </SideText>
            </SideContainer>
            <ResponsiveImage
              src={Image14}
              alt="연합해커톤"
              maxWidth="850px"
              maxHeight="301px"
            />
          </SideImage>

          <Start>
            13기 아기사자 여러분, 1년동안 열정적으로 참여하시어 꼭{" "}
            <span>수료</span>하시길 바랍니다!
          </Start>
        </Contents>

        <First>
          <Start>한국외대(글로벌) 멋사에 대해 조금 더 알고 싶다면?</Start>
          <SocialContainer>
            <SocialItem
              onClick={() =>
                handleSocialClick(
                  "https://www.instagram.com/hufsglobal_likelion/"
                )
              }
            >
              <SocialIcon>
                <BsInstagram />
              </SocialIcon>
              <SocialText>
                <span>Instagram</span>&nbsp; hufs global
              </SocialText>
            </SocialItem>
            <SocialItem
              onClick={() =>
                handleSocialClick("https://velog.io/@hufsglobal09/posts")
              }
            >
              <SocialIcon>
                <SiVelog />
              </SocialIcon>
              <SocialText>
                <span>Velog</span>&nbsp; hufs global
              </SocialText>
            </SocialItem>
          </SocialContainer>
        </First>
      </Container>
    </Frame>
  );
}
