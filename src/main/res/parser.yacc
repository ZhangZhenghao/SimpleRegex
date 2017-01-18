%code imports {
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.sine_x.regexp.exeception.RegexException;
import com.sine_x.regexp.state.*;
import com.sine_x.regexp.Pattern;
}

%language "Java"
%parse-param {Pattern pattern}
%define public
%define package com.sine_x.regexp.parser
%define parser_class_name {Parser}
%define throws {RegexException}

%token DIGIT
%token CHAR
%type <Character> CHAR DIGIT exp_char exp_op rep_op set_char set_op '+' '?' '*' '[' ']' '{' '}' '(' ')' '.' ',' '-' '^' '\\'
%type <Integer> number
%type <Repeater> repeater
%type <Range> item
%type <RangeSet<Character>> items
%type <Fragment> alternations concatenations closed
%type <CharClassState> set

%left '+' '*' '?' '{'
%left '|'

%%

exp:
  %empty                            { pattern.setStart(new MatchedState());     }
| alternations                      { pattern.setStart($1.getStart());
                                      $1.patch(new MatchedState());             }

alternations:
  concatenations                    { $$ = $1;                  }
| concatenations '|' alternations   { $$ = $1.alternates($3);   }

concatenations:
  closed                            { $$ = $1;                  }
| closed concatenations             { $$ = $1.concatenate($2);  }

closed:
  exp_char						    { $$ = new Fragment(new CharState($1)); 	}
| set 							    { $$ = new Fragment($1);					}
| '.'							    { $$ = new Fragment(new WildCardState());	}
| closed repeater                   { $$ = $2.repeat($1);                       }
| '(' alternations ')'              { $$ = $2;                                  }

exp_char:
  CHAR 							    { $$ = $1;	}
| DIGIT							    { $$ = $1;	}
| set_op                            { $$ = $1;  }
| rep_op                            { $$ = $1;  }
| '\\' exp_op   				    { $$ = $2;	}
| '\\' '\\'						    { $$ = $2;	}

/* Character */

exp_op:
  '+'							{ $$ = $1;	}
| '?'							{ $$ = $1;	}
| '*'							{ $$ = $1;	}
| '{'							{ $$ = $1;	}
| '['							{ $$ = $1;	}
| '.'							{ $$ = $1;	}
| ')'							{ $$ = $1;	}

set_op:
  '^'							{ $$ = $1;	}
| '-'							{ $$ = $1;	}
| ']'							{ $$ = $1;	}

rep_op:
  ','							{ $$ = $1;	}
| '}'							{ $$ = $1;	}

/* Repeater */

repeater:
  '+'							{ $$ = Repeater.REP_MORE_THAN_ONCE;				}
| '*'							{ $$ = Repeater.REP_ANY_TIME;					}
| '?'							{ $$ = Repeater.REP_NONE_OR_ONCE;				}
| '{' number '}'				{ $$ = new Repeater($2, Repeater.NUM_NONE);		}
| '{' number ',' '}'			{ $$ = new Repeater($2, Repeater.NUM_INFINITY);	}
| '{' number ',' number '}'		{ $$ = new Repeater($2, $4);					}

number:
  DIGIT							{ $$ = $1 - '0';				}
| number DIGIT 					{ $$ = $1 * 10 + ($2 - '0');	}

/* Character set */

set:
  '[' items ']'					{ $$ = new CharClassState($2);				}
| '[' '^' items ']'				{ $$ = new CharClassState($3, true);		}

items:
  item 							{ RangeSet<Character> set = TreeRangeSet.create(); set.add($1); $$ = set;	}
| item items					{ RangeSet<Character> set = $2; set.add($1); $$ = set;						}

item:
  set_char						{ $$ = Range.closed($1, $1);	}
| set_char '-' set_char			{ $$ = Range.closed($1, $3);	}

set_char:
  CHAR 							{ $$ = $1;	}
| DIGIT							{ $$ = $1;	}
| exp_op						{ $$ = $1;	}
| rep_op						{ $$ = $1;	}
| '\\' set_op					{ $$ = $2;	}
| '\\' '\\'						{ $$ = $2;	}
