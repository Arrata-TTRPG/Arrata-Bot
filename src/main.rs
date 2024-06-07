use anyhow::Context as _;
use arrata_bot::Command;
use arrata_lib::{roll_stat, Obstacle};
use clap::Parser;
use itertools::Itertools;
use serenity::async_trait;
use serenity::model::channel::Message;
use serenity::model::gateway::Ready;
use serenity::prelude::*;
use shuttle_runtime::SecretStore;
use tracing::{error, info};

struct Bot;

#[async_trait]
impl EventHandler for Bot {
    async fn message(&self, ctx: Context, msg: Message) {
        if msg.content.starts_with("!a") || msg.content.starts_with("!A") {
            let message = msg.content.split(' ').collect::<Vec<&str>>();

            info!("Got message: {:?}", message);

            match Command::try_parse_from(message) {
                Ok(message) => {
                    match message {
                        Command::Roll(r) => {
                            let result = roll_stat(
                                &r.stat,
                                r.advantage.unwrap_or_default(),
                                r.disadvantage.unwrap_or_default(),
                            );

                            let mut response = String::from("Rolling `");

                            if let Some(a) = r.advantage {
                                response.push('!');
                                if a > 1 {
                                    response.push_str(format!("{a}").as_str());
                                }
                            }

                            if let Some(d) = r.disadvantage {
                                response.push('?');
                                if d > 1 {
                                    response.push_str(format!("{d}").as_str());
                                }
                            }

                            response.push_str(format!("{}", r.stat).as_str());

                            if let Some(Obstacle(ob)) = r.ob {
                                response.push_str(format!(" vs Ob {}", ob).as_str());
                            }

                            response.push('`');

                            response.push_str(
                                format!(
                                    "\n```{}\n\nSuccesses: {}\nFailures:  {}",
                                    result.results.iter().format(", "),
                                    result.successes,
                                    result.failures,
                                )
                                .as_str(),
                            );

                            if let Some(Obstacle(ob)) = r.ob {
                                if result.successes >= ob.try_into().unwrap() {
                                    response.push_str(
                                        format!("\n\nSuccess!   {} >= {}", result.successes, ob)
                                            .as_str(),
                                    );
                                } else {
                                    response.push_str(
                                        format!("\n\nFailure... {} < {}", result.successes, ob)
                                            .as_str(),
                                    );
                                }
                            }

                            response.push_str("```");

                            if let Err(e) = msg.channel_id.say(&ctx.http, response).await {
                                error!("Error sending message: {:?}", e);
                            }
                        }
                    };
                }
                Err(err) => {
                    error!("Error reading message: {}", err);
                }
            }
        }
    }

    async fn ready(&self, _: Context, ready: Ready) {
        info!("{} is connected!", ready.user.name);
    }
}

#[shuttle_runtime::main]
async fn serenity(
    #[shuttle_runtime::Secrets] secrets: SecretStore,
) -> shuttle_serenity::ShuttleSerenity {
    // Get the discord token set in `Secrets.toml`
    let token = secrets
        .get("DISCORD_TOKEN")
        .context("'DISCORD_TOKEN' was not found")?;

    // Set gateway intents, which decides what events the bot will be notified about
    let intents = GatewayIntents::GUILD_MESSAGES | GatewayIntents::MESSAGE_CONTENT;

    let client = Client::builder(&token, intents)
        .event_handler(Bot)
        .await
        .expect("Err creating client");

    Ok(client.into())
}
