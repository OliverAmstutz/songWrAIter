import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import {HashRouter, Route, Routes} from 'react-router-dom';
import Header from './header/Header';
import {ToastContainer} from 'react-toastify';
import ErrorBoundary from './error/ErrorBoundary';
import ErrorPage from './error/ErrorPage';
import SongPage from './components/SongPage.tsx';
import {QueryClient, QueryClientProvider} from '@tanstack/react-query';

const queryClient = new QueryClient()

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <QueryClientProvider client={queryClient}>
            <ToastContainer/>
            <HashRouter>
                <Header/>
                <ErrorBoundary>
                    <Routes>
                        <Route path="/" element={<SongPage/>}/>
                        <Route path="*" element={<ErrorPage statusCode={400}
                                                            errorMessage={'Page not found'}/>}/>
                    </Routes>
                </ErrorBoundary>
            </HashRouter>
        </QueryClientProvider>
    </React.StrictMode>
);