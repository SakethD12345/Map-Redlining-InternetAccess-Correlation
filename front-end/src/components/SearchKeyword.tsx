import {
  ChangeEventHandler,
  FormEventHandler,
  useEffect,
  useRef,
  useState,
} from "react";

import "../App.css";
import { fetchData, isFeatureCollection } from "../overlays";
import { isServerErrorResponse } from "../utils/types";

interface InputBoxProps {
  setState: (data: GeoJSON.FeatureCollection | undefined) => void;
}

// Function to render a form that contains an input box and a submit button
export default function InputBox({ setState }: InputBoxProps) {
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

    let userInput = formState.keyword.trim().replace(" ", "+");
    setErrorText("");

    let url = `${SERVER_URL}/areaSearch?area=${userInput}`;

    const data = await Promise.resolve(fetchData(url));

    if (isFeatureCollection(data)) setState(data);
  };

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
