import {
  ChangeEventHandler, Dispatch,
  FormEventHandler, SetStateAction,
  useEffect,
  useRef,
  useState,
} from "react";

import "../App.css";
import { fetchData, isFeatureCollection } from "../overlays";
import { isServerErrorResponse } from "../utils/types";

interface InputBoxProps {
  data: string | undefined
  setData: Dispatch<SetStateAction<string | undefined>>;
  broadbandPercentage: string | undefined;
  setBroadbandPercentage: Dispatch<SetStateAction<string | undefined>>;
}

// Function to render a form that contains an input box and a submit button
export default function InputBox( props : InputBoxProps) {
  const [formState, setFormState] = useState({
    keyword: "",
  });

  const [errorText, setErrorText] = useState("");

  const SERVER_URL = "http://localhost:2025";

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setFormState({
      ...formState,
      [e.target.name]: value,
    });
  };

  /*
  Function to handle update state
   */
  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const inputArray = formState.keyword.trim().split(" ");
    if(inputArray[0] == "broadband") {
      broadband(inputArray).then((r) => props.setBroadbandPercentage(r))
    }
    let userInput = formState.keyword.trim().replace("_", "%20");

    setErrorText("");


    search(userInput)
    .then((r) => isFeatureCollection(r) ? r : undefined)
    .then((r) => props.setData(r))
  };

  async function search(input: string): Promise<string> {
    return await fetch("http://localhost:2025/areaSearch?area=" + input)
    .then((r) => r.json())
    .then((r) => {
      {
        return r
      }
    })
  }

  async function broadband(inputArray: string[]): Promise<string> {
    var state = inputArray[1].replace("_", "%20")
    var county = inputArray[2] + "_County"
    return await fetch(
        SERVER_URL +
        "/broadband?State=" +
        state +
        "&County=" +
        county
    )
    .then((r) => r.json())
    .then((response) => {
      if (!(response["result"] == "success")) {
        return response["error_type"];
      } else {
        return "% Broadband Access: " + response["householdPercentage"];
      }
    });
  }

  return (
      <form onSubmit={handleSubmit} role="form" className="keyword-search-form">
        <label htmlFor="keyword-input">
          Keyword
          <input
              id="keyword-input"
              type="text"
              name="keyword"
              role="keywordinput"
              aria-label="Keyword Searchbox"
              value={formState.keyword}
              onChange={handleInputChange}
              className="form_input_box"
          />
        </label>

        <button
            className="command-button"
            type="submit"
            role="search-keyword-button"
            aria-label="Keyword Search Submit Button"
        >
          Search
        </button>
        <div className="error-text" role="keyword-search-error">
          {errorText}
        </div>
      </form>
  );
}