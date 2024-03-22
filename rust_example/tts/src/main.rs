use serde::Serialize;

const TTS_URL: &str = "https://open.mobvoi.com/api/tts/v1";
const APP_KEY: &str = "";
const APP_SECRET: &str = "";

#[derive(Serialize, Debug, Clone, Default)]
struct PostData {
    signature: String,
    timestamp: String,
    appkey: String,
    speaker: String,
    ignore_limit: bool,
    gen_srt: bool,
    audio_type: String,
    text: String,
    speed: String,
}

impl PostData {
    fn new() -> PostData {
        PostData {
            signature: "appkey+secret+timestamp的MD5值".to_string(),
            timestamp: "".to_string(),
            appkey: APP_KEY.to_string(),
            speaker: "cissy_meet".to_string(),
            ignore_limit: true,
            gen_srt: false,
            audio_type: "wav".to_string(),
            text: "深度学习是机器学习的分支，是一种以人工神经网络为架构，对资料进行表征学习的算法。度学习中的形容词“深度”是指在网络中使用多层。早期的工作表明，线性感知器不能成为通用分类器，但具有非多项式激活函数和一个无限宽度隐藏层的网络可以成为通用分类器。".to_string(),
            speed: "1.1".to_string(),
        }
    }
}

fn get_time_now() -> String {
    use std::time::SystemTime;
    let now = SystemTime::now()
        .duration_since(SystemTime::UNIX_EPOCH)
        .unwrap()
        .as_secs();
    return now.to_string();
}

fn gen_md5sum(origin: String) -> String {
    let result = md5::compute(origin.as_bytes());
    println!("md5sum: {:x}", result);
    return format!("{:x}", result).to_string();
}

async fn synthesis() -> Result<(), reqwest::Error> {
    println!("Begin to synthesis text");
    let mut post_data = PostData::new();
    post_data.timestamp = get_time_now();
    post_data.signature = gen_md5sum(String::from(
        post_data.appkey.clone() + "+" + APP_SECRET + "+" + &post_data.timestamp,
    ));
    println!("post_data: {:?}", post_data);
    let client = reqwest::Client::new();
    let res = client
        .post(TTS_URL)
        .header("Content-type", "application/json")
        .body(serde_json::to_string(&post_data).unwrap())
        .send()
        .await?;
    if !res.status().is_success() {
        println!("Failed to synthesis text {:?}", res);
        return Ok(());
    }
    let body = res.bytes().await?;
    use std::fs::File;
    use std::io::Write;
    let mut file = File::create("output.wav").unwrap();
    file.write_all(&body).unwrap();
    println!("Synthesis text success");
    Ok(())
}

#[tokio::main]
async fn main() -> Result<(), reqwest::Error> {
    synthesis().await
}
