//! Roll commands, typically in the form, `!roll B5`

use arrata_lib::character::Stat;
use clap::Parser;

#[derive(Debug, Clone, Parser)]
pub struct Roll {
    #[arg(short)]
    pub advantage: Option<usize>,
    #[arg(short)]
    pub disadvantage: Option<usize>,
    #[arg(value_parser = clap::value_parser!(Stat))]
    pub stat: Stat,
}
