#![warn(clippy::all, clippy::pedantic)]

use clap::Parser;

pub mod char;
pub use char::Char;
pub mod roll;
pub use roll::Roll;

#[derive(Debug, Clone, Parser)]
#[clap(name = "!a", version, alias("!A"))]
pub struct App {
    #[clap(subcommand)]
    pub command: Command,
}

#[derive(Debug, Clone, Parser)]
pub enum Command {
    #[command(alias = "r")]
    Roll(Roll),
}
