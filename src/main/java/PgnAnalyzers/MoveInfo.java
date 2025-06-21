package PgnAnalyzers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoveInfo {
    public String piece;
    public String location;
    public boolean capture;
    public String destination;
    public boolean check;
    public boolean checkmate;
    public String promotion;
    public boolean castlingKing;
    public boolean castlingQueen;
    public boolean enPassant;

    public void decipherMove(String move){
        String positionsRegex = "[a-h][1-8]";
        String piece = "";
        String location = "";
        String capture = "";
        String destination = "";
        String promotion = "";
        String check = "";
        String checkmate = "";
        String castlingKing = "";
        String castlingQueen = "";
        String enPassant = "";

        if (move.contains("o-o-o") || move.contains("O-O-O") || move.contains("0-0-0")){
            castlingQueen = "0-0-0";
        }
        else if (move.contains("o-o") || move.contains("O-O") || move.contains("0-0")){
            castlingKing = "0-0";
        }
        else{
            Pattern pattern = Pattern.compile(positionsRegex);
            Matcher matcher = pattern.matcher(move);

            List<String> matches = new ArrayList<>();

            while (matcher.find()) {
                matches.add(matcher.group());
            }
            //check which piece is moving

            if (matches.size() > 1){
                location = matches.get(0);
                destination = matches.get(1);
            }else if (matches.size() == 1){
//                System.out.println(matches.get(0));
                destination = matches.getFirst();
                location = "";
            }else{
                destination = "";
                location = "";
            }

            if (Character.isUpperCase(move.charAt(0))){
                piece = move.charAt(0)+"";
            }else{
                piece = "P";
            }

            if (location.isEmpty() && !destination.isEmpty()){
                String disambiguateRank;
                String disambiguateFile;
                if (Character.isDigit(move.charAt(0)) && piece.equals("P")) {
                    disambiguateRank = move.charAt(0) + "";
                }else if (Character.isDigit(move.charAt(1)) && !piece.equals("P")) {
                    disambiguateRank = move.charAt(1)+"";
                }else{
                    disambiguateRank = "";
                }

                if (Character.isLowerCase(move.charAt(0)) && !Character.isDigit(move.charAt(1)) && piece.equals("P") && move.charAt(0) != 'x') {
                    disambiguateFile = move.charAt(0)+"";
                }
                else if(Character.isLowerCase(move.charAt(1)) && !Character.isDigit(move.charAt(2)) && move.charAt(1) != 'x') {
                    disambiguateFile = move.charAt(1)+"";
                }else{
                    disambiguateFile = "";
                }
                location = disambiguateFile + disambiguateRank;
            }
        }

        if(move.contains("x")){
            capture = "x";
        }

        if(move.contains("=")){
            int index = move.indexOf("=");
            promotion = move.substring(index,index+2);
        }

        if(move.contains("#")){
            checkmate = "#";
        }

        if(move.contains("+")){
            check = "+";
        }

        if(move.contains("e.p.") || move.contains("ep")){
            enPassant = "e.p.";
        }

        this.piece =  piece;
        this.location =  location;
        this.capture = !capture.isEmpty();
        this.destination =  destination;
        this.check =  !check.isEmpty();
        this.checkmate =   !checkmate.isEmpty();
        this.promotion =  promotion;
        this.castlingKing = !castlingKing.isEmpty();
        this.castlingQueen =   !castlingQueen.isEmpty();
        this.enPassant =  !enPassant.isEmpty();
    }
}
