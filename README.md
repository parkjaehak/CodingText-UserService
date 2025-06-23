# 👨‍💻 코딩테스트 블로그 서비스 CodingText

![image](https://github.com/user-attachments/assets/3c7b0591-87bd-476a-aac6-6c9ac1d0d059)
![image](https://github.com/user-attachments/assets/9e3dea35-3c08-47fe-94c1-e7f6982c6ed7)


<br>

## 프로젝트 소개

CodingText는 코딩테스트에 참여하고 이를 바탕으로 게시글을 작성할 수 있는, 코딩테스트 블로그 서비스 입니다.
- **문제 풀이 기록과 블로그 통합**: 문제 풀이 과정과 학습 내용을 기록하고, 관련 게시글을 쉽게 확인할 수 있습니다.
- **AI 기반 문제 생성**: OpenAI API를 통해 난이도, 알고리즘 유형 등 사용자 맞춤형 문제를 생성합니다.
- **검색 및 필터 기능**: 다양한 필터와 대화형 AI를 활용하여 필요한 문제와 정보를 효율적으로 탐색할 수 있습니다.
- **종합 도구 제공**: 문제 풀이를 위한 코드 에디터, 컴파일러, 메모장 등 학습에 필요한 도구를 한 페이지에서 제공합니다.
- **문제 기여 기능**: 생성한 AI 문제를 정식 등록 건의를 통해 플랫폼에 기여할 수 있습니다.

<br>

## 팀원 구성

<div align="center">

| **윤장호** | **김규리** | **김민서** | **박재학** | **최효성** | **이규동** | **박정재** | **이동석** |
| :------: |  :------: | :------: | :------: | :------: |  :------: | :------: | :------: |
| [<img width="142" alt="image" src="https://github.com/user-attachments/assets/c4ffbabf-abd7-4aba-b23f-b699f72d7de6" /> <br/> @seorang42](https://github.com/seorang42) | [<img width="142" alt="image" src="https://github.com/user-attachments/assets/3637dde4-584a-4464-bc35-a28001a61d26" /> <br/> @lamiiiii](https://github.com/lamiiiii) | [<img width="142" alt="image" src="https://github.com/user-attachments/assets/fb65203c-72c4-4d75-bd4c-5a1158c0c781" /> <br/> @hurrayPersimmon](https://github.com/hurrayPersimmon) | [<img width="142" alt="image" src="https://github.com/user-attachments/assets/2ecfd50a-52a4-44ee-9cf9-f2c193dc1f4c" /> <br/> @parkjaehak](https://github.com/parkjaehak) | [<img width="143" alt="image" src="https://github.com/user-attachments/assets/af3a44a6-ace0-4d8d-913a-9f32c7f008d0" /> <br/> @hyoseong-Choi](https://github.com/hyoseong-Choi) | [<img width="142" alt="image" src="https://github.com/user-attachments/assets/15040167-0261-4a11-bfd1-5a02933b21c3" /> <br/> @starboxxxx](https://github.com/starboxxxx) | [<img width="142" alt="image" src="https://github.com/user-attachments/assets/eb327efe-2ca4-47bc-9ee4-ba4cb9df01dd" /> <br/> @j-ash0224](https://github.com/j-ash0224) | [<img width="143" alt="image" src="https://github.com/user-attachments/assets/e45ef734-857b-4878-a231-73ea5e70c3c7" /> <br/> @DaveLee-b](https://github.com/DaveLee-b) |

</div>

<br>

## 개발 환경
- Frontend : Next.js, React, TypeScript
- Backend : Java, Spring
- Infrastructure : Xen Orchestra,Docker, AWS
- Database : Redis, Elasticsearch, Amazon RDS, MySQL
- CI/CD : Github, Jenkins
- 협업 툴 : Notion, Jira, Github, Slack
- 서비스 배포 환경: On-premises, Spring Cloud
- 디자인 : [Figma](https://www.figma.com/ko-kr/)

<br>

## 개발 기간
 -  2024-09-10 ~ 2024-12-20

<br>

## 브랜치 전략

**Git Flow**를 기반으로 설정하여, 각 브랜치가 명확한 역할을 갖도록 했습니다. 이 전략은 협업 시 효율적인 작업 분담과 코드 통합을 가능하게 했습니다

- **main**: 배포 환경에 최적화된 안정적인 코드만을 관리하기 위해 main 브랜치를 배포 단계에서만 사용했습니다.
- **develop**: develop 브랜치는 개발 단계에서 Git Flow의 master 역할을 하며, 모든 기능이 통합되는 주요 브랜치로 설정했습니다. 기능 개발이 완료되면 각 feature 브랜치에서 develop 브랜치로 병합하여 전체 프로젝트의 통합 상태를 관리했습니다.
- **feature**: 각 기능 단위로 독립적인 개발을 진행하기 위해 feature 브랜치를 사용했습니다. 이를 통해 다른 기능 개발에 영향을 미치지 않고 독립적으로 작업할 수 있었으며, 기능 개발이 완료되면 develop 브랜치에 병합하고, 병합 후에는 feature 브랜치를 삭제하여 깔끔한 브랜치 관리를 할 수 있었습니다.

<br>


## 페이지별 기능

### [회원가입]

| 소셜 로그인 |
|----------|
|<img width="258" alt="image" src="https://github.com/user-attachments/assets/48cbe1c9-f0a7-45e9-9603-99be904aebbe" />|

| 회원가입 |
|----------|
|<img width="257" alt="image" src="https://github.com/user-attachments/assets/4111ed96-e05d-4ea5-b7b7-fed083436626" />|

<br>

### [메인페이지]
| 배너/상단바 |
|----------|
|![그림1](https://github.com/user-attachments/assets/bd280f69-701b-4fe1-9f47-567e022c5cc2)|

| 인기 게시글 |
|----------|
|![image](https://github.com/user-attachments/assets/561553f8-a1fa-40cf-b887-e51bbb4af104)|


<br>

| 초기화면 (데스크탑) |
|----------------|
|<img width="529" alt="image" src="https://github.com/user-attachments/assets/348974d6-a29d-4c47-b25a-e2dd4381e60a" />|

| 초기화면 (모바일) |
|---------------|
|<img width="201" alt="image" src="https://github.com/user-attachments/assets/c94a6569-8221-4c8e-9220-ba5919aece06" />|

<br>

## 향후 방향성

- **더 많은 언어 지원**: Kotlin, Go 등 다양한 프로그래밍 언어에 대한 지원을 확장하여, 사용자가 보다 다양한 언어로 작업할 수 있는 환경을 제공할 것입니다.
- **개인화된 서비스**: 사용자의 실력을 정밀하게 파악하고, 부족한 부분을 개선할 수 있도록 맞춤형 문제를 생성하고 추천하는 서비스를 제공할 것입니다.
- **자체 AI 모델 개발**: OpenAI API를 활용하지 않고, 서비스에 특화된 AI 모델을 개발하여 더욱 개인화된 경험을 제공할 것입니다.



## 프로젝트 후기


### 윤장호

여러 새로운 라이브러리를 사용해볼 수 있었고, 기존에 알고 있던 기술도 더 단단히 할 수 있는 좋은 계기가 되었습니다. 또한 혼자서는 경험할 수 없는 여러 시행착오를 겪으며 성장할 수 있었고, 여러 분야의 팀원과의 협업을 통해 백엔드, 인프라 등 다양한 관점에서의 지식이 늘어 이후 협업에서도 큰 도움이 될 것 같습니다.

### 김규리

이번 프로젝트를 통해 TypeScript와 Next.js를 주로 사용하며 실무적인 역량을 키웠습니다. 예상치 못한 에러와 복잡한 예외 상황 속에서도 팀원들과 함께 해결 방안을 모색하며 성과를 냈고, 열정적인 협업 덕분에 단순히 결과물이 아니라 팀워크의 가치를 깊이 느낀 소중한 경험이었습니다.

### 김민서

이전 프로젝트에서 개선할 점을 갖고 실행하고자 PM으로 참여하여 MSA, k8s, 젠킨스 등 여러 기술을 접한 기회였습니다. 기술적으로 잘 알지 못하여 JIRA에서 티켓조차 나눠주지 못해 팀원들에게 미안합니다. 그래도 부족한 PM두고도 훌륭한 프로젝트로 마무리할 수 있게 도와준 팀원들에게 감사하고 싶습니다.

### 박재학

스프링 클라우드를 통한 msa 구축, CI/CD를 파이프라인 구축을 통한 devops 적용 등의 기술을 습득할 수 있었습니다. 또한 팀원들과 매일같이 프로젝트에 대해 피드백을 주고받으며 지식을 팀원들에게 전달하는 방법과 다른 팀원들이 전달해주는 내용을 잘 이해할 수 있는 능력을 키울 수 있었습니다.

### 최효성

코딩테스트 기능, 카프카로 CDC, 엘라스틱서치로 검색 등을 개발하면서 일반적인 crud가 아닌 색다른 기능을 개발할 수 있어 신선했고, 새로운 것들을 많이 배워갈 수 있는 프로젝트였습니다.

### 이규동

블로그 서비스 부분을 담당하여 개발을 진행해보았는데 고려해야 될 부분이 생각보다 많았습니다. 특히 이미지 처리와 관련해서는 많은 어려움이 있었지만 멘토님들께서 친절하게 피드백을 작성 해주셔서 많은 도움이 되었습니다. 개발 뿐만 아니라 다양한 협업 툴도 접해보는 등 많은 부분에서 크게 성장한 것 같습니다.

### 박정재

이번 프로젝트는 서로 다른 개성을 가진 팀원들과 협력하며 많은 것을 배운 값진 시간이었습니다. 처음 도전하는 기술과 환경에서 어려움도 있었지만, 이를 극복하며 성장할 수 있었습니다. 앞으로도 이 경험을 발판 삼아 목표를 이루기 위해 꾸준히 노력하고자 합니다!

### 이동석

처음에 아카데미에 지원하면서 잘 할수있을까 걱정이 많았지만 훌륭한 팀원들과 인연이 닿아 함께 프로젝트를 진행할 수 있어서 정말 행복했고 동시에 많은걸 얻어갈 수 있는 시간이었습니다!
